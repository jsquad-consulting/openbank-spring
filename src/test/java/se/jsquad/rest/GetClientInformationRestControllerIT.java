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

package se.jsquad.rest;

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
import org.springframework.http.HttpStatus;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import se.jsquad.client.info.ClientApi;
import se.jsquad.client.info.ClientData;
import se.jsquad.client.info.ClientRequest;
import se.jsquad.client.info.TypeApi;

import java.io.File;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@Execution(ExecutionMode.SAME_THREAD)
public class GetClientInformationRestControllerIT {
    private Gson gson = new Gson();

    private static int servicePort = 8443;

    private static DockerComposeContainer dockerComposeContainer = new DockerComposeContainer(
            new File("docker-compose.yaml"))
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
    }

    @AfterAll
    static void destroyDocker() {
        dockerComposeContainer.stop();
    }

    @Test
    public void testGetClientInformation() {
        // Given
        String personIdentificationNumber = "191212121212";

        // When
        Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .get(URI.create("/client/info/" + personIdentificationNumber)).andReturn();

        ClientApi clientApi = gson.fromJson(response.getBody().print(), ClientApi.class);

        // Then
        assertEquals(200, response.getStatusCode());

        assertEquals(personIdentificationNumber, clientApi.getPerson().getPersonIdentification());
        assertEquals("John", clientApi.getPerson().getFirstName());
        assertEquals("Doe", clientApi.getPerson().getLastName());
        assertEquals(personIdentificationNumber, clientApi.getPerson().getPersonIdentification());
        assertEquals("john.doe@test.se", clientApi.getPerson().getMail());

        assertEquals(1, clientApi.getAccountList().size());
        assertEquals(500.0, clientApi.getAccountList().get(0).getBalance());

        assertEquals(1, clientApi.getAccountList().get(0).getAccountTransactionList().size());
        assertEquals("DEPOSIT", clientApi.getAccountList().get(0).getAccountTransactionList().get(0)
                .getTransactionType().name());
        assertEquals("500$ in deposit", clientApi.getAccountList().get(0).getAccountTransactionList().get(0)
                .getMessage());

        assertEquals(TypeApi.REGULAR, clientApi.getClientType().getType());
        assertEquals(500, clientApi.getClientType().getRating());
    }

    @Test
    public void testGetClientInformationWithInvalidPersonIdenticationNumber() {
        // Given
        String personIdentificationNumber = "123";

        // When
        Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .get(URI.create("/client/info/" + personIdentificationNumber)).andReturn();


        // Then
        assertEquals(HttpStatus.BAD_REQUEST, HttpStatus.valueOf(response.getStatusCode()));
        assertEquals("Person identification number must be twelve digits.",
                response.getBody().print());
    }

    @Test
    public void testGetClientInformationByClientRequestBody() {
        // Given
        ClientRequest clientRequest = new ClientRequest();
        clientRequest.setClientData(new ClientData());

        clientRequest.getClientData().setPersonIdentificationNumber("191212121212");

        // When
        Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .body(gson.toJson(clientRequest))
                .get(URI.create("/get/client/info/")).andReturn();

        ClientApi clientApi = gson.fromJson(response.getBody().print(), ClientApi.class);

        // Then
        assertEquals(200, response.getStatusCode());

        assertEquals(clientRequest.getClientData().getPersonIdentificationNumber(),
                clientApi.getPerson().getPersonIdentification());
        assertEquals("John", clientApi.getPerson().getFirstName());
        assertEquals("Doe", clientApi.getPerson().getLastName());
        assertEquals(clientRequest.getClientData().getPersonIdentificationNumber(),
                clientApi.getPerson().getPersonIdentification());
        assertEquals("john.doe@test.se", clientApi.getPerson().getMail());

        assertEquals(1, clientApi.getAccountList().size());
        assertEquals(500.0, clientApi.getAccountList().get(0).getBalance());

        assertEquals(1, clientApi.getAccountList().get(0).getAccountTransactionList().size());
        assertEquals("DEPOSIT", clientApi.getAccountList().get(0).getAccountTransactionList().get(0)
                .getTransactionType().name());
        assertEquals("500$ in deposit", clientApi.getAccountList().get(0).getAccountTransactionList().get(0)
                .getMessage());

        assertEquals(TypeApi.REGULAR, clientApi.getClientType().getType());
        assertEquals(500, clientApi.getClientType().getRating());
    }

    @Test
    public void testGetClientInformationWithRequestBodyConstraints() {
        // Given
        ClientRequest clientRequest = null;

        // When
        Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .body(gson.toJson(clientRequest))
                .get(URI.create("/get/client/info/")).andReturn();


        // Then
        assertEquals(HttpStatus.BAD_REQUEST, HttpStatus.valueOf(response.getStatusCode()));
    }
}