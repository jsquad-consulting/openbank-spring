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
import org.mockserver.client.MockServerClient;
import org.mockserver.model.Delay;
import org.mockserver.model.Header;
import org.springframework.http.HttpStatus;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import se.jsquad.client.info.ClientApi;
import se.jsquad.client.info.ClientRequest;
import se.jsquad.client.info.TypeApi;
import se.jsquad.client.info.WorldApiResponse;

import java.io.File;
import java.net.URI;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

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
            .withTailChildContainers(false) // set to true for trace purpose when things fails
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
    public void testGetHelloWorldRestResponse() {
        // Given
        WorldApiResponse worldApiResponse = new WorldApiResponse();
        worldApiResponse.setMessage("Hello world");

        new MockServerClient(dockerComposeContainer.getServiceHost("worldapi_1", 1080),
                dockerComposeContainer.getServicePort("worldapi_1", 1080))
                .when(request()
                        .withMethod("GET")
                        .withPath("/api/get/hello/world"))
                        .respond(response()
                        .withStatusCode(200)
                        .withHeaders(new Header("Content-Type", "application/json"))
                        .withBody(gson.toJson(worldApiResponse))
                        .withDelay(new Delay(TimeUnit.SECONDS, 1)));

        // When
        Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .get(URI.create("/get/hello/world")).andReturn();

        // Then
        WorldApiResponse worldApiResponseResult = gson.fromJson(response.getBody().print(), WorldApiResponse.class);

        assertEquals(200, response.getStatusCode());

        assertEquals("Hello world", worldApiResponseResult.getMessage());
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
        clientRequest.setPersonIdentificationNumber("191212121212");

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

        assertEquals(clientRequest.getPersonIdentificationNumber(), clientApi.getPerson().getPersonIdentification());
        assertEquals("John", clientApi.getPerson().getFirstName());
        assertEquals("Doe", clientApi.getPerson().getLastName());
        assertEquals(clientRequest.getPersonIdentificationNumber(), clientApi.getPerson().getPersonIdentification());
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