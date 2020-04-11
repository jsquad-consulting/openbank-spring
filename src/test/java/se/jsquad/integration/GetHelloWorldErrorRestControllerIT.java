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
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.jasypt.util.text.BasicTextEncryptor;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.Delay;
import org.mockserver.model.Header;
import org.springframework.http.MediaType;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;
import java.net.URI;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@Testcontainers
@Execution(ExecutionMode.SAME_THREAD)
public class GetHelloWorldErrorRestControllerIT {
    private Gson gson = new Gson();

    private static MockServerClient mockServerClient;

    private static int servicePort = 8443;

    private static DockerComposeContainer dockerComposeContainer = new DockerComposeContainer(
            new File("src/test/resources/docker-compose-int.yaml"))
            .withExposedService("openbank_1", servicePort)
            .withExposedService("worldapi_1", 1080)
            .withPull(false)
            .withTailChildContainers(true) // set to true for trace purpose when things fails
            .withLocalCompose(true);

    @BeforeAll
    static void setupDocker() {
        dockerComposeContainer.start();

        RestAssured.baseURI = "https://" + dockerComposeContainer.getServiceHost("openbank_1", servicePort);
        RestAssured.port = dockerComposeContainer.getServicePort("openbank_1", servicePort);
        RestAssured.basePath = "/api";

        String encryptedPassword = "RMiukf/2Ir2Dr1aTGd0J4CXk6Y/TyPMN";

        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        textEncryptor.setPassword(System.getenv("MASTER_KEY"));

        RestAssured.trustStore("src/test/resources/test/ssl/truststore/jsquad.jks",
                textEncryptor.decrypt(encryptedPassword));

        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        mockServerClient = new MockServerClient(dockerComposeContainer.getServiceHost("worldapi_1", 1080),
                dockerComposeContainer.getServicePort("worldapi_1", 1080));
    }

    @AfterAll
    static void destroyDocker() {
        dockerComposeContainer.stop();
    }

    @Test
    public void testGetHelloWorldRestResponseServerError() throws InterruptedException {
        // Given
        mockServerClient
                .when(request()
                        .withMethod("GET")
                        .withPath("/api/get/hello/world"))
                .respond(response()
                        .withStatusCode(500)
                        .withHeaders(new Header("Content-Type", MediaType.TEXT_PLAIN_VALUE))
                        .withBody("Internal server error")
                        .withDelay(new Delay(TimeUnit.SECONDS, 1)));

        // When
        Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .get(URI.create("/get/hello/world")).andReturn();

        // Then
        assertEquals(500, response.getStatusCode());

        assertEquals("Webclient is not available at this time.", response.getBody().print());
    }
}