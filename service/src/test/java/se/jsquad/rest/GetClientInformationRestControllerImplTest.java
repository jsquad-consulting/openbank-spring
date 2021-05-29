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

package se.jsquad.rest;

import com.google.gson.Gson;
import nl.altindag.log.LogCaptor;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.apache.activemq.broker.BrokerService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.reactive.function.client.WebClient;
import se.jsquad.api.client.AccountApi;
import se.jsquad.api.client.AccountTransactionApi;
import se.jsquad.api.client.ClientApi;
import se.jsquad.api.client.ClientData;
import se.jsquad.api.client.ClientRequest;
import se.jsquad.api.client.TransactionTypeApi;
import se.jsquad.api.client.WorldApiResponse;
import se.jsquad.component.database.FlywayDatabaseMigration;
import se.jsquad.configuration.ApplicationConfiguration;

import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@TestPropertySource(locations = {"classpath:test/application.properties",
        "classpath:activemq.properties",
        "classpath:test/configuration/configuration_test.properties",
        "classpath:test/configuration/openbank_jpa.properties",
        "classpath:test/configuration/security_jpa.properties"},
        properties = {"jasypt.encryptor.password = testencryption"})
@SpringBootTest
@Transactional(transactionManager = "transactionManagerOpenBank", propagation = Propagation.REQUIRED)
@Execution(ExecutionMode.SAME_THREAD)
public class GetClientInformationRestControllerImplTest {
    static String baseUrl;

    @Configuration
    @Import(ApplicationConfiguration.class)
    public static class TestConfig {
        @Bean("WorldApiWebClient")
        WebClient getWorldApiClient() {
            return WebClient.builder().baseUrl(baseUrl)
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .defaultUriVariables(Collections.singletonMap("url", baseUrl))
                    .build();
        }
    }

    private static MockWebServer mockBackEnd;
    
    @BeforeAll
    public static void init() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
        baseUrl = String.format("http://localhost:%s",
                mockBackEnd.getPort());
    }
    
    @AfterAll
    static void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }

    @MockBean
    private BrokerService brokerService;

    @MockBean
    private FlywayDatabaseMigration flywayDatabaseMigration;

    @Autowired
    private GetClientInformationRestController getClientInformationRESTController;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private WebApplicationContext context;

    private Gson gson = new Gson();
    
    @Test
    public void testGetHelloWorldByMockedRemoteRestfulServer() {
        // Given
        WorldApiResponse worldApiResponse = new WorldApiResponse();
        worldApiResponse.setMessage("Hello world");
        mockBackEnd.enqueue(new MockResponse()
                .setBody(gson.toJson(worldApiResponse))
                .addHeader("Content-Type", "application/json"));

        // When
        ResponseEntity<WorldApiResponse> worldApiResponseResponseEntity =
                getClientInformationRESTController.getHelloWorld();

        // Then
        assertEquals(HttpStatus.OK, worldApiResponseResponseEntity.getStatusCode());

        WorldApiResponse worldApiResponseResult = worldApiResponseResponseEntity.getBody();

        assertNotNull(worldApiResponseResult);
        assertEquals("Hello world", worldApiResponseResult.getMessage());
    }

    @Test
    public void testGetClientInformationByRequestBody() throws Exception {
        // Given
        LogCaptor logCaptor = LogCaptor.forClass(GetClientInformationRestController.class);
        logCaptor.setLogLevelToInfo();
        
        ClientRequest clientRequest = new ClientRequest();

        clientRequest.setClientData(new ClientData());
        clientRequest.getClientData().setPersonIdentificationNumber("191212121212");

        // When
        ResponseEntity<ClientApi> responseEntity =
                getClientInformationRESTController.getClientInformationByRequestBody(clientRequest);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        ClientApi clientApi = responseEntity.getBody();

        assertEquals(clientRequest.getClientData().getPersonIdentificationNumber(),
                clientApi.getPerson().getPersonIdentification());
        assertEquals("John", clientApi.getPerson().getFirstName());
        assertEquals("Doe", clientApi.getPerson().getLastName());
        assertEquals("john.doe@test.se", clientApi.getPerson().getMail());

        assertEquals(500, clientApi.getClientType().getRating());

        AccountApi accountApi = clientApi.getAccountList().get(0);

        assertEquals(500, accountApi.getBalance());

        AccountTransactionApi accountTransactionApi = accountApi.getAccountTransactionList().get(0);

        assertEquals("500$ in deposit", accountTransactionApi.getMessage());
        assertEquals(TransactionTypeApi.DEPOSIT, accountTransactionApi.getTransactionType());
    
        assertEquals(2, logCaptor.getInfoLogs().size());
        assertTrue(logCaptor.getInfoLogs().get(0).contains("se.jsquad.api.client.ClientRequest@"));
        assertEquals("Finish method getClientInformationByRequestBody", logCaptor.getInfoLogs().get(1));
        
        // Given
        clientRequest.getClientData().setPersonIdentificationNumber("");

        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

        // When and then
        mockMvc.perform(get("/api/get/client/info/").contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(gson.toJson(clientRequest))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Person identification number must be twelve digits."));

        // Given
        clientRequest.getClientData().setPersonIdentificationNumber(null);

        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

        // When
        MvcResult mvcResult = mockMvc.perform(get("/api/get/client/info/")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(gson.toJson(clientRequest))
            .accept(MediaType.APPLICATION_JSON))
            .andReturn();
    
        // Then
        assertEquals(400, mvcResult.getResponse().getStatus());
        assertEquals("Person identification number must be twelve digits.", mvcResult.getResponse()
            .getContentAsString());

        // Given
        clientRequest.setClientData(null);

        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

        // When and then
        mockMvc.perform(get("/api/get/client/info/").contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(gson.toJson(clientRequest))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Person identification number must be twelve digits."));

        // Given
        clientRequest = null;

        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

        // When and then
        mockMvc.perform(get("/api/get/client/info/").contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(gson.toJson(clientRequest))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(""));
    
        logCaptor.clearLogs();
    }

    @Test
    public void testGetClientInformation() {
        // Given
        LogCaptor logCaptor = LogCaptor.forClass(GetClientInformationRestController.class);
        logCaptor.setLogLevelToInfo();
        
        String personIdentification = "191212121212";

        // When
        ResponseEntity<ClientApi> responseEntity =
                getClientInformationRESTController.getClientInformation(personIdentification);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        ClientApi clientApi = responseEntity.getBody();

        assertEquals(personIdentification, clientApi.getPerson().getPersonIdentification());
        assertEquals("John", clientApi.getPerson().getFirstName());
        assertEquals("Doe", clientApi.getPerson().getLastName());
        assertEquals("john.doe@test.se", clientApi.getPerson().getMail());

        assertEquals(500, clientApi.getClientType().getRating());

        AccountApi accountApi = clientApi.getAccountList().get(0);

        assertEquals(500, accountApi.getBalance());

        AccountTransactionApi accountTransactionApi = accountApi.getAccountTransactionList().get(0);

        assertEquals("500$ in deposit", accountTransactionApi.getMessage());
        assertEquals(TransactionTypeApi.DEPOSIT, accountTransactionApi.getTransactionType());
    
        assertEquals(2, logCaptor.getInfoLogs().size());
        assertEquals("getClientInformation(191212121212)", logCaptor.getInfoLogs().get(0));
        assertEquals("Finish method getClientInformation", logCaptor.getInfoLogs().get(1));
        
        logCaptor.clearLogs();
        
    }

    @Test
    public void testResponseEntityPersonIdentificationNumberInvalid() throws Exception {
        // Given
        String personIdentificationNumber = "123";
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

        // When and then
        mockMvc.perform(get("/api/client/info/" + personIdentificationNumber)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Person identification number must be twelve digits."));
    }

    @Test
    public void testInvalidPersonIdentificationNumberEmpty() {
        // Given
        String personalIdentificationNumber = "";

        // When
        Throwable throwable = assertThrows(ConstraintViolationException.class, () ->
                getClientInformationRESTController.getClientInformation(personalIdentificationNumber));

        // Then
        assertEquals("Person identification number must be twelve digits.",
                ((ConstraintViolationException) throwable).getConstraintViolations().iterator().next().getMessage());
    }

    @Test
    public void testInvalidPersonIdentificationNumberLessThenTwelveDigits() {
        // Given
        String personalIdentificationNumber = "123";

        // When
        Throwable throwable = assertThrows(ConstraintViolationException.class, () ->
                getClientInformationRESTController.getClientInformation(personalIdentificationNumber));

        // Then
        assertEquals("Person identification number must be twelve digits.",
                ((ConstraintViolationException) throwable).getConstraintViolations().iterator().next().getMessage());
    }

    @Test
    public void testSingleton() {
        // Given
        GetClientInformationRestController getClientInformationRestController1 = (GetClientInformationRestController)
                applicationContext.getBean(
                        "getClientInformationRestController");

        GetClientInformationRestController getClientInformationRestController2 = (GetClientInformationRestController)
                applicationContext.getBean(
                        "getClientInformationRestController");

        // Then
        assertEquals(getClientInformationRestController1, getClientInformationRestController2);
    }
}
