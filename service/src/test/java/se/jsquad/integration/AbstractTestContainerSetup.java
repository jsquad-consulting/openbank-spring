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
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static se.jsquad.integration.RyukIntegration.CONTAINER_ENVIRONMENT;
import static se.jsquad.integration.RyukIntegration.DEFAULT_NAME_SPACE;
import static se.jsquad.integration.RyukIntegration.DOCKER_COMPOSE_SERVICE_NAME_ENDING;
import static se.jsquad.integration.RyukIntegration.KUBERNETES_STARTER_SERVICE_NAME;
import static se.jsquad.integration.RyukIntegration.LOCALHOST;
import static se.jsquad.integration.RyukIntegration.MOCK_SERVER_PORT;
import static se.jsquad.integration.RyukIntegration.OPENBANK_DATABASE;
import static se.jsquad.integration.RyukIntegration.OPENBANK_MONITORING;
import static se.jsquad.integration.RyukIntegration.OPENBANK_SERVICE;
import static se.jsquad.integration.RyukIntegration.SECURITY_DATABASE;
import static se.jsquad.integration.RyukIntegration.setupRestAssured;

@Testcontainers
@Execution(ExecutionMode.SAME_THREAD)
public class AbstractTestContainerSetup {
    private static DockerComposeContainer dockerComposeContainerStarter;
    private static final Duration TWENTY_MINUTES = Duration.ofMinutes(20);

    private static List<V1Deployment> scaledDeployments = new ArrayList();
    
    protected Gson gson = new Gson();
    protected static AppsV1Api appsV1Api;
    protected static CoreV1Api coreV1Api;
    
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
                    .withExposedService(OPENBANK_DATABASE.getServiceName(), OPENBANK_DATABASE.getPort(),
                        waitFor20Minutes())
                    .withExposedService(SECURITY_DATABASE.getServiceName(), SECURITY_DATABASE.getPort(),
                        waitFor20Minutes())
                    .withExposedService(OPENBANK_SERVICE.getServiceName(), OPENBANK_SERVICE.getPort(),
                        waitFor20Minutes())
                    .withExposedService(OPENBANK_MONITORING.getServiceName(), OPENBANK_MONITORING.getPort(),
                        waitFor20Minutes());
                dockerComposeContainerStarter.start();
                dockerComposeContainer = dockerComposeContainerStarter;
            } else if (CONTAINER_ENVIRONMENT.equals("KUBERNETES")) {
                dockerComposeContainerStarter = new
                    DockerComposeContainer(new File("src/test/resources/" +
                    "docker-compose-k8-integration.yaml"))
                    .withLocalCompose(true)
                    .withPull(false)
                    .withTailChildContainers(false)
                    .waitingFor(KUBERNETES_STARTER_SERVICE_NAME,
                        Wait.forHealthcheck().withStartupTimeout(TWENTY_MINUTES));
                dockerComposeContainerStarter.start();
                dockerComposeContainer = dockerComposeContainerStarter;
                
                ContainerState containerState = (ContainerState) dockerComposeContainer
                    .getContainerByServiceName(KUBERNETES_STARTER_SERVICE_NAME
                        + DOCKER_COMPOSE_SERVICE_NAME_ENDING).get();
                
                containerState.copyFileFromContainer("/root/.kube/config", inputStream -> {
                    ApiClient apiClient = Config.fromConfig(inputStream);
                    apiClient.setVerifyingSsl(false);
                    
                    Configuration.setDefaultApiClient(apiClient);
                    
                    appsV1Api = new AppsV1Api();
                    coreV1Api = new CoreV1Api();
                    return null;
                });
            }
            
            setupRestAssured();
            setupMockServerClient();
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
    
    protected URI toURI(final String path, final String protocol, final RyukIntegration.RyukService ryukService)
        throws MalformedURLException, URISyntaxException {
        
        ContainerState containerState = (ContainerState) dockerComposeContainer
            .getContainerByServiceName(ryukService.getServiceName() + DOCKER_COMPOSE_SERVICE_NAME_ENDING).get();
    
        final String mappedUri = containerState.getHost();
        final int mappedPort;
    
        if (CONTAINER_ENVIRONMENT.equals("DOCKER")) {
            mappedPort = dockerComposeContainer.getServicePort(ryukService.getServiceName(),
                ryukService.getPort());
        } else {
            mappedPort = ryukService.getPort();
        }
        
        return new URL(protocol, mappedUri, mappedPort, path).toURI();
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
        List<File> directories = new ArrayList<File>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            directories.add(new File(resource.getFile()));
        }
        
        ArrayList<Class> classes = new ArrayList<Class>();
        for (File directory : directories) {
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
