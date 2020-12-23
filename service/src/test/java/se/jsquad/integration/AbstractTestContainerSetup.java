/*
 * Copyright 2020 JSquad AB
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

import com.google.gson.Gson;
import io.restassured.RestAssured;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jasypt.util.text.BasicTextEncryptor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mockserver.client.MockServerClient;
import org.rnorth.ducttape.ratelimits.RateLimiter;
import org.rnorth.ducttape.ratelimits.RateLimiterBuilder;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.containers.wait.strategy.WaitStrategy;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Testcontainers
@Execution(ExecutionMode.SAME_THREAD)
public class AbstractTestContainerSetup {
    protected Gson gson = new Gson();
    private static final Duration TEN_MINUTES = Duration.ofMinutes(10);
    private static final Logger logger = LogManager.getLogger(AbstractTestContainerSetup.class);
    
    protected static final String SERVICE_NAME = "openbank";
    protected static final String WORLD_API_NAME = "worldapi";
    private static final String OPENBANK_DATABASE = "openbankdb";
    private static final String SECURITY_DATABASE = "securitydb";
    
    protected static final int SERVICE_PORT = 8443;
    protected static final int MONITORING_PORT = 8081;
    private static final int WORLD_API_PORT = 1080;
    private static final int MYSQL_PORT = 3306;
    
    protected static final String BASE_PATH_API = "/api";
    protected static final String BASE_PATH_ACTUATOR = "/actuator";
    
    protected static final String PROTOCOL_HTTPS = "https://";
    protected static final String PROTOCOL_HTTP = "http://";
    
    protected static MockServerClient mockServerClient;
    
    private static final RateLimiter every5Seconds = RateLimiterBuilder.newBuilder()
        .withRate(10, TimeUnit.MINUTES)
        .withConstantThroughput().build();
    
    private static final WaitStrategy waitFor10Minutes() {
        return Wait.forListeningPort()
            .withRateLimiter(every5Seconds)
            .withStartupTimeout(TEN_MINUTES);
    }
    
    public static DockerComposeContainer dockerComposeContainer;
    
    private static void setupRestAssured() {
        String encryptedPassword = "RMiukf/2Ir2Dr1aTGd0J4CXk6Y/TyPMN";
        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        textEncryptor.setPassword(System.getenv("MASTER_KEY"));
        RestAssured.trustStore("src/test/resources/test/ssl/truststore/jsquad.jks",
            textEncryptor.decrypt(encryptedPassword));
    
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }
    
    protected static void setupMockServerClient() {
        mockServerClient = new MockServerClient(dockerComposeContainer.getServiceHost(WORLD_API_NAME, WORLD_API_PORT),
            dockerComposeContainer.getServicePort(WORLD_API_NAME, WORLD_API_PORT));
    }
    
    public void setupEndPointRestAssured(final String protocol, final String serviceName, final int servicePort,
                                         final String basePath) {
        RestAssured.baseURI = protocol + dockerComposeContainer.getServiceHost(serviceName, servicePort);
        RestAssured.port = dockerComposeContainer.getServicePort(serviceName, servicePort);
        RestAssured.basePath = basePath;
    }
    
    @BeforeAll
    static void setUpDockerComposeContainers() {
        if (dockerComposeContainer == null) {
                dockerComposeContainer = new
                    DockerComposeContainer(new File("src/test/resources/docker-compose-int.yaml"))
                    .withLocalCompose(true)
                    .withPull(false)
                    .withTailChildContainers(false)
                    .withExposedService(OPENBANK_DATABASE, MYSQL_PORT, waitFor10Minutes())
                    .withExposedService(SECURITY_DATABASE, MYSQL_PORT, waitFor10Minutes())
                    .withExposedService(WORLD_API_NAME, WORLD_API_PORT, waitFor10Minutes())
                    .withExposedService(SERVICE_NAME, SERVICE_PORT, waitFor10Minutes())
                    .withExposedService(SERVICE_NAME, MONITORING_PORT, waitFor10Minutes());
                dockerComposeContainer.start();
            setupRestAssured();
            setupMockServerClient();
        }
    }
}
