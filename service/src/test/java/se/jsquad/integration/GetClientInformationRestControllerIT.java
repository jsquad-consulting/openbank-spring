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

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import se.jsquad.api.client.ClientApi;
import se.jsquad.api.client.ClientData;
import se.jsquad.api.client.ClientInformationRequest;
import se.jsquad.api.client.ClientInformationResponse;
import se.jsquad.api.client.ClientRequest;
import se.jsquad.api.client.PersonApi;
import se.jsquad.api.client.TypeApi;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static se.jsquad.integration.RyukIntegration.BASE_PATH_API;
import static se.jsquad.integration.RyukIntegration.OPENBANK_SERVICE;
import static se.jsquad.integration.RyukIntegration.PROTOCOL_HTTPS;
import static se.jsquad.interceptor.RequestHeaderInterceptor.CORRELATION_ID_HEADER_NAME;
import static se.jsquad.interceptor.RequestHeaderInterceptor.X_AUTHORIZATION_HEADER_NAME;
import static se.jsquad.util.ClientTestCredentials.CLIENT_NAME;
import static se.jsquad.util.ClientTestCredentials.CLIENT_PASSWORD;

public class GetClientInformationRestControllerIT extends AbstractTestContainerSetup {
    @Test
    void updateClientInformation() throws MalformedURLException, URISyntaxException {
        // Given
        ClientInformationRequest clientInformationRequest = new ClientInformationRequest();

        clientInformationRequest.setPerson(new PersonApi());
        String personIdentification = "191212121212";

        clientInformationRequest.getPerson().setPersonIdentification(personIdentification);

        // When
        Response response = RestAssured
                .given()
                .header(CORRELATION_ID_HEADER_NAME, "b48bb267-04e8-4cba-97ac-01e8898de372")
                .header(X_AUTHORIZATION_HEADER_NAME, java.util.Base64.getEncoder()
                    .encodeToString((CLIENT_NAME + ":" + CLIENT_PASSWORD).getBytes()))
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(clientInformationRequest)
                .when()
                .put(toURI(BASE_PATH_API + "/update/client/information/", PROTOCOL_HTTPS, OPENBANK_SERVICE))
            .andReturn();

        ClientInformationResponse clientApiResponse = gson.fromJson(response.getBody().asString(),
                ClientInformationResponse.class);

        // Then
        assertEquals(200, response.getStatusCode());
        assertEquals(personIdentification, clientApiResponse.getPerson().getPersonIdentification());
    }

    @Test
    void updateClientInformationWithBadContent() throws MalformedURLException, URISyntaxException {
        // Given
        ClientInformationRequest clientInformationRequest = new ClientInformationRequest();

        clientInformationRequest.setPerson(new PersonApi());
        String personIdentification = "19121212121";

        clientInformationRequest.getPerson().setPersonIdentification(personIdentification);

        // When
        Response response = RestAssured
                .given()
                .header(CORRELATION_ID_HEADER_NAME, "1149df8a-7e37-4971-9ce3-271621bba2ba")
                .header(X_AUTHORIZATION_HEADER_NAME, java.util.Base64.getEncoder()
                    .encodeToString((CLIENT_NAME + ":" + CLIENT_PASSWORD).getBytes()))
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(clientInformationRequest)
                .when()
                .put(toURI(BASE_PATH_API + "/update/client/information/", PROTOCOL_HTTPS, OPENBANK_SERVICE))
            .andReturn();

        // Then
        assertEquals(400, response.getStatusCode());
        assertEquals("Person identification number must be twelve digits.", response.getBody().asString());
    }

    @Test
    void testGetClientInformation() throws MalformedURLException, URISyntaxException {
        // Given
        String personIdentificationNumber = "191212121212";

        // When
        Response response = RestAssured
                .given()
                .header(CORRELATION_ID_HEADER_NAME, "fa0996f5-4bc4-4abb-b137-2b1bc89f607e")
                .header(X_AUTHORIZATION_HEADER_NAME, java.util.Base64.getEncoder()
                    .encodeToString((CLIENT_NAME + ":" + CLIENT_PASSWORD).getBytes()))
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .get(toURI(BASE_PATH_API + "/client/info/" + personIdentificationNumber, PROTOCOL_HTTPS,
                    OPENBANK_SERVICE)).andReturn();

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
    void testGetClientInformationWithInvalidPersonIdentificationNumber() throws MalformedURLException, URISyntaxException {
        // Given
        String personIdentificationNumber = "123";

        // When
        Response response = RestAssured
                .given()
                .header(CORRELATION_ID_HEADER_NAME, "b92a4c2f-3ba3-41f7-a6d8-eb813f3a0151")
                .header(X_AUTHORIZATION_HEADER_NAME, java.util.Base64.getEncoder()
                    .encodeToString((CLIENT_NAME + ":" + CLIENT_PASSWORD).getBytes()))
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .get(toURI(BASE_PATH_API + "/client/info/" + personIdentificationNumber, PROTOCOL_HTTPS,
                    OPENBANK_SERVICE)).andReturn();


        // Then
        assertEquals(HttpStatus.BAD_REQUEST, HttpStatus.valueOf(response.getStatusCode()));
        assertEquals("Person identification number must be twelve digits.",
                response.getBody().print());
    }

    @Test
    void testGetClientInformationByClientRequestBody() throws MalformedURLException, URISyntaxException {
        // Given
        ClientRequest clientRequest = new ClientRequest();
        clientRequest.setClientData(new ClientData());

        clientRequest.getClientData().setPersonIdentificationNumber("191212121212");

        // When
        Response response = RestAssured
                .given()
                .header(CORRELATION_ID_HEADER_NAME, "a80c3b71-a2c0-48be-b3e2-9087033491a0")
                .header(X_AUTHORIZATION_HEADER_NAME, java.util.Base64.getEncoder()
                    .encodeToString((CLIENT_NAME + ":" + CLIENT_PASSWORD).getBytes()))
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .body(gson.toJson(clientRequest))
                .get(toURI(BASE_PATH_API + "/get/client/info/", PROTOCOL_HTTPS, OPENBANK_SERVICE)).andReturn();

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
    void testGetClientInformationWithRequestBodyConstraints() throws MalformedURLException, URISyntaxException {
        // Given
        ClientRequest clientRequest = null;

        // When
        Response response = RestAssured
                .given()
                .header(CORRELATION_ID_HEADER_NAME, "d86e2b8e-866a-4132-aaba-18063c7c5543")
                .header(X_AUTHORIZATION_HEADER_NAME, Base64.getEncoder()
                    .encodeToString((CLIENT_NAME + ":" + CLIENT_PASSWORD).getBytes()))
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .body(gson.toJson(clientRequest))
                .get(toURI(BASE_PATH_API + "/get/client/info/", PROTOCOL_HTTPS, OPENBANK_SERVICE)).andReturn();


        // Then
        assertEquals(HttpStatus.BAD_REQUEST, HttpStatus.valueOf(response.getStatusCode()));
    }
}