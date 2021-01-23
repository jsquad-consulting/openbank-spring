/*
 * Copyright 2021 JSquad AB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package se.jsquad.integration;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.google.gson.Gson;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1DeploymentList;
import io.kubernetes.client.openapi.models.V1DeploymentSpec;
import io.kubernetes.client.util.Config;
import io.restassured.RestAssured;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jasypt.util.text.BasicTextEncryptor;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mockserver.client.MockServerClient;
import org.rnorth.ducttape.ratelimits.RateLimiter;
import org.rnorth.ducttape.ratelimits.RateLimiterBuilder;
import org.testcontainers.containers.ContainerState;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.containers.wait.strategy.WaitStrategy;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Testcontainers
@Execution(ExecutionMode.SAME_THREAD)
public class AbstractTestContainerSetup {
    private static DockerComposeContainer dockerComposeContainerStarter;
    private static final Duration TWENTY_MINUTES = Duration.ofMinutes(20);
    private static final int HTTP_K8_PORT = 80;
    private static final int HTTPS_K8_PORT = 443;
    private static final int POSTGRES_PORT = 5432;
    private static final int MOCK_SERVER_PORT = 1080;
    private static final Logger logger = LogManager.getLogger(AbstractTestContainerSetup.class);
    private static final String DEFAULT_NAME_SPACE = "default";
    private static final String LOCALHOST = "localhost";
    private static final String OPENBANK_DATABASE = "openbankdb";
    private static final String SECURITY_DATABASE = "securitydb";
    private static List<V1Deployment> scaledDeployments = new ArrayList();
    
    protected Gson gson = new Gson();
    protected static AppsV1Api appsV1Api;
    protected static CoreV1Api coreV1Api;
    protected static final int MONITORING_PORT = 8081;
    protected static final int SERVICE_PORT = 8443;
    protected static final String BASE_PATH_ACTUATOR = "/actuator";
    protected static final String BASE_PATH_API = "/api";
    protected static final String CONTAINER_ENVIRONMENT = System.getenv("CONTAINER_ENVIRONMENT");
    protected static final String PROTOCOL_HTTP = "http://";
    protected static final String PROTOCOL_HTTPS = "https://";
    protected static final String SERVICE_NAME = "openbank";
    protected static MockServerClient mockServerClient;
    protected static int NUMBER_OF_INTEGRATION_TESTS;
    protected static int NUMBER_OF_EXECUTED_TESTS = 0;
    
    public static DockerComposeContainer dockerComposeContainer;
    
    @BeforeAll
    static void setUpDockerComposeContainers() throws IOException, ClassNotFoundException {
        if (dockerComposeContainer == null) {
            NUMBER_OF_INTEGRATION_TESTS = countNumberOfTests();
            
            if (CONTAINER_ENVIRONMENT.equals("DOCKER")) {
                dockerComposeContainerStarter = new
                    DockerComposeContainer(new File("src/test/resources/docker-compose-int.yaml"))
                    .withLocalCompose(true)
                    .withPull(false)
                    .withTailChildContainers(false)
                    .withExposedService(OPENBANK_DATABASE, POSTGRES_PORT, waitFor20Minutes())
                    .withExposedService(SECURITY_DATABASE, POSTGRES_PORT, waitFor20Minutes())
                    .withExposedService(SERVICE_NAME, SERVICE_PORT, waitFor20Minutes())
                    .withExposedService(SERVICE_NAME, MONITORING_PORT, waitFor20Minutes());
                dockerComposeContainerStarter.start();
                dockerComposeContainer = dockerComposeContainerStarter;
                
                setupRestAssured();
                setupMockServerClient();
            } else if (CONTAINER_ENVIRONMENT.equals("KUBERNETES")) {
                dockerComposeContainerStarter = new
                    DockerComposeContainer(new File("src/test/resources/" +
                    "docker-compose-k8-integration.yaml"))
                    .withLocalCompose(true)
                    .withPull(false)
                    .withTailChildContainers(false)
                    .waitingFor("kubernetes-starter_1", Wait.forHealthcheck()
                        .withStartupTimeout(TWENTY_MINUTES));
                dockerComposeContainerStarter.start();
                dockerComposeContainer = dockerComposeContainerStarter;
                
                setupRestAssured();
                setupMockServerClient();
                
                ContainerState containerState = (ContainerState) dockerComposeContainer
                    .getContainerByServiceName("kubernetes-starter_1").get();
                
                containerState.copyFileFromContainer("/root/.kube/config", inputStream -> {
                    ApiClient apiClient = Config.fromConfig(inputStream);
                    apiClient.setVerifyingSsl(false);
                    
                    Configuration.setDefaultApiClient(apiClient);
                    
                    appsV1Api = new AppsV1Api();
                    coreV1Api = new CoreV1Api();
                    return null;
                });
            }
        }
    }
    
    @AfterAll
    static void stopDockerComposeContainer() throws NoSuchMethodException, InvocationTargetException,
        IllegalAccessException, NoSuchFieldException {
        if (NUMBER_OF_EXECUTED_TESTS == NUMBER_OF_INTEGRATION_TESTS) {
            Field field = DockerComposeContainer.class.getDeclaredField("dockerClient");
            field.setAccessible(true);
    
            DockerClient dockerClient = (DockerClient) field.get(dockerComposeContainer);
    
            for (Container container: dockerClient.listContainersCmd().exec()) {
                if (container.toString().contains("rancher")){
                    dockerClient.killContainerCmd(container.getId()).exec();
                }
            }
    
            Method method = DockerComposeContainer.class.getDeclaredMethod("runWithCompose", String.class);
            method.setAccessible(true);
            method.invoke(dockerComposeContainer, "stop");
        }
    }
    
    @AfterEach
    public void afterEachTest() {
        ++NUMBER_OF_EXECUTED_TESTS;
    }
    
    protected static void setupMockServerClient() {
        mockServerClient = new MockServerClient(LOCALHOST, MOCK_SERVER_PORT);
    }
    
    protected void setupEndpointForRestAssuredAdapterHttps() {
        if (CONTAINER_ENVIRONMENT.equals("DOCKER")) {
            setupEndPointRestAssured(PROTOCOL_HTTPS, SERVICE_NAME, SERVICE_PORT, BASE_PATH_API);
        } else if (CONTAINER_ENVIRONMENT.equals("KUBERNETES")) {
            setupEndPointRestAssured(PROTOCOL_HTTPS, LOCALHOST, HTTPS_K8_PORT, BASE_PATH_API);
        }
    }
    
    protected void setupEndpointForRestAssuredAdapterHttp() {
        if (CONTAINER_ENVIRONMENT.equals("DOCKER")) {
            setupEndPointRestAssured(PROTOCOL_HTTP, SERVICE_NAME, MONITORING_PORT, BASE_PATH_ACTUATOR);
        } else if (CONTAINER_ENVIRONMENT.equals("KUBERNETES")) {
            setupEndPointRestAssured(PROTOCOL_HTTP, LOCALHOST, HTTP_K8_PORT, BASE_PATH_ACTUATOR);
        }
    }
    
    protected void executeContainerPodCLI(String service, String command) throws NoSuchMethodException,
        InvocationTargetException, IllegalAccessException, ApiException {
        if (CONTAINER_ENVIRONMENT.equals("DOCKER")) {
            Method method = DockerComposeContainer.class.getDeclaredMethod("runWithCompose", String.class);
            method.setAccessible(true);
            if (command.equals("START")) {
                method.invoke(dockerComposeContainer, "start " + service);
            } else if (command.equals("KILL")) {
                method.invoke(dockerComposeContainer, "kill " + service);
            }
        } else if (CONTAINER_ENVIRONMENT.equals("KUBERNETES")) {
            if (command.equals("START")) {
                scaleDeployment(service + "-deployment", 1);
            } else if (command.equals("KILL")) {
                scaleDeployment(service + "-deployment", 0);
            }
        }
    }
    
    private static void scaleDeployment(String deploymentName, int numberOfReplicas) throws ApiException {
        V1DeploymentList v1DeploymentList = appsV1Api.listNamespacedDeployment(DEFAULT_NAME_SPACE, null,
            null, null, null, null, null,
            null, null, null, Boolean.FALSE);
        
        List<V1Deployment> v1DeploymentListItems = v1DeploymentList.getItems();
        
        Optional<V1Deployment> optionalCurrentV1Deployment =
            v1DeploymentListItems
                .stream()
                .filter(
                    (V1Deployment deployment) ->
                        deployment.getMetadata().getName().equals(deploymentName))
                .findFirst();
        
        if (optionalCurrentV1Deployment.isPresent()) {
            if (numberOfReplicas == 0) {
                scaledDeployments.add(optionalCurrentV1Deployment.get());
            }
            V1DeploymentSpec newSpec = optionalCurrentV1Deployment.get().getSpec().replicas(numberOfReplicas);
            V1Deployment newDeploy = optionalCurrentV1Deployment.get().spec(newSpec);
            appsV1Api.replaceNamespacedDeployment(
                deploymentName, DEFAULT_NAME_SPACE, newDeploy, null, null, null);
        } else {
            Optional<V1Deployment> optionalOldV1Deployment = scaledDeployments.stream()
                .filter(v1Deployment -> v1Deployment.getMetadata().getName().equals(deploymentName)).findFirst();
            if (optionalOldV1Deployment.isPresent()) {
                V1DeploymentSpec newSpec = optionalOldV1Deployment.get().getSpec().replicas(numberOfReplicas);
                V1Deployment newDeploy = optionalOldV1Deployment.get().spec(newSpec);
                scaledDeployments.remove(optionalOldV1Deployment.get());
                appsV1Api.replaceNamespacedDeployment(
                    deploymentName, DEFAULT_NAME_SPACE, newDeploy, null, null, null);
            }
        }
    }
    
    private static final RateLimiter every5Seconds = RateLimiterBuilder.newBuilder()
        .withRate(10, TimeUnit.MINUTES)
        .withConstantThroughput().build();
    
    private static final WaitStrategy waitFor20Minutes() {
        return Wait.forListeningPort()
            .withRateLimiter(every5Seconds)
            .withStartupTimeout(TWENTY_MINUTES);
    }
    
    
    private static void setupRestAssured() {
        String encryptedPassword = "RMiukf/2Ir2Dr1aTGd0J4CXk6Y/TyPMN";
        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        textEncryptor.setPassword(System.getenv("MASTER_KEY"));
        RestAssured.trustStore("src/test/resources/test/ssl/truststore/jsquad.jks",
            textEncryptor.decrypt(encryptedPassword));
        
        if (CONTAINER_ENVIRONMENT.equals("KUBERNETES")) {
            RestAssured.useRelaxedHTTPSValidation();
        }
        
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }
    
    private void setupEndPointRestAssured(final String protocol, final String serviceName, final int servicePort,
                                          final String basePath) {
        if (CONTAINER_ENVIRONMENT.equals("DOCKER")) {
            RestAssured.baseURI = protocol + dockerComposeContainer.getServiceHost(serviceName, servicePort);
            RestAssured.port = dockerComposeContainer.getServicePort(serviceName, servicePort);
        } else if (CONTAINER_ENVIRONMENT.equals("KUBERNETES")) {
            RestAssured.baseURI = protocol + serviceName;
            RestAssured.port = servicePort;
        }
        
        RestAssured.basePath = basePath;
    }
    
    private static int countNumberOfTests() throws IOException, ClassNotFoundException {
        ArrayList<Class> classes = new ArrayList();
        
        classes.addAll(getClasses("se.jsquad.integration"));
        
        int testcaseCount = 0;
        for (Class cl : classes) {
            Method[] methods = cl.getDeclaredMethods();
            
            for (Method method : methods) {
                Annotation[] annotations = method.getDeclaredAnnotations();
                for (Annotation annotation : annotations) {
                    if (annotation.annotationType().toString()
                        .equals("interface org.junit.jupiter.api.Test")) {
                        testcaseCount++;
                    }
                }
            }
        }
        return testcaseCount;
    }
    
    private static List<Class> getClasses(String packageName)
        throws ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<File>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        
        ArrayList<Class> classes = new ArrayList<Class>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes;
    }
    
    private static List<Class> findClasses(File directory, String packageName)
        throws ClassNotFoundException {
        List<Class> classes = new ArrayList<Class>();
        if (!directory.exists()) {
            return classes;
        }
        
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName + '.'
                    + file.getName().substring(0, file.getName().length() - 6)));
            }
        }
        return classes;
    }
}
