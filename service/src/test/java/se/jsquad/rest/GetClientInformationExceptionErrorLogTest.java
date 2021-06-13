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

import nl.altindag.log.LogCaptor;
import okhttp3.mockwebserver.MockWebServer;
import org.apache.activemq.broker.BrokerService;
import org.jose4j.base64url.Base64;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.reactive.function.client.WebClient;
import se.jsquad.AbstractSpringBootConfiguration;
import se.jsquad.business.OpenBankService;
import se.jsquad.component.database.FlywayDatabaseMigration;
import se.jsquad.configuration.ApplicationConfiguration;

import java.io.IOException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static se.jsquad.interceptor.RequestHeaderInterceptor.CORRELATION_ID_HEADER_NAME;
import static se.jsquad.interceptor.RequestHeaderInterceptor.X_AUTHORIZATION_HEADER_NAME;
import static se.jsquad.util.ClientTestCredentials.CLIENT_NAME;
import static se.jsquad.util.ClientTestCredentials.CLIENT_PASSWORD;

public class GetClientInformationExceptionErrorLogTest extends AbstractSpringBootConfiguration {
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
    private WebApplicationContext webApplicationContext;
    
    @MockBean
    private OpenBankService openBankService;
    
    @Test
    public void testGetClientInformationLoggerExceptionErrorWithDebugEnabled() throws Exception {
        // Given
        Mockito.when(openBankService.getClientInformationByPersonIdentification(anyString()))
            .thenThrow(new RuntimeException("System failed totally by getting the personal identification " +
                "number!"));
        
        final String CORRELATION_ID = "980fda45-2f14-44ab-939d-46020d028ef3";
        LogCaptor logCaptorClientInformation = LogCaptor.forClass(GetClientInformationRestController.class);
        logCaptorClientInformation.setLogLevelToDebug();
        
        LogCaptor logCaptorRestResponseExceptionHandler = LogCaptor.forClass(RestResponseEntityExceptionHandler.class);
        logCaptorRestResponseExceptionHandler.setLogLevelToDebug();
        
        String personIdentification = "191212121212";
    
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        // When
        MvcResult mvcResult = mockMvc.perform(get("/api/client/info/" + personIdentification)
            .header(CORRELATION_ID_HEADER_NAME, CORRELATION_ID)
            .header(X_AUTHORIZATION_HEADER_NAME, Base64.encode((CLIENT_NAME + ":" + CLIENT_PASSWORD).getBytes()))
            .accept(MediaType.APPLICATION_JSON)).andReturn();
    
        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), mvcResult.getResponse().getStatus(),
            "Failed to assert status code 500 " + mvcResult.getResponse().getContentAsString());
    
        assertEquals(1, logCaptorClientInformation.getInfoLogs().size());
        assertEquals(0, logCaptorClientInformation.getDebugLogs().size());
        assertEquals(0, logCaptorClientInformation.getErrorLogs().size());
        assertEquals(0, logCaptorClientInformation.getTraceLogs().size());
        assertEquals(0, logCaptorClientInformation.getWarnLogs().size());
    
        assertEquals(3, logCaptorRestResponseExceptionHandler.getInfoLogs().size());
        assertEquals(0, logCaptorRestResponseExceptionHandler.getDebugLogs().size());
        assertEquals(1, logCaptorRestResponseExceptionHandler.getErrorLogs().size());
        assertEquals(0, logCaptorRestResponseExceptionHandler.getTraceLogs().size());
        assertEquals(0, logCaptorRestResponseExceptionHandler.getWarnLogs().size());
    
        assertEquals("getClientInformation(CLIENT_NAME: client1,CORRELATION_ID: " +
            "980fda45-2f14-44ab-939d-46020d028ef3,191212121212)", logCaptorClientInformation.getInfoLogs().get(0));
        
        assertEquals("handleGlobalException(CLIENT_NAME: client1,CORRELATION_ID: " +
            "980fda45-2f14-44ab-939d-46020d028ef3,java.lang.RuntimeException: " +
            "System failed totally by getting the personal identification number!,ServletWebRequest: " +
            "uri=/api/client/info/191212121212;client=127.0.0.1)",
            logCaptorRestResponseExceptionHandler.getInfoLogs().get(0));
        assertEquals("Method handleGlobalException response(CLIENT_NAME: client1,CORRELATION_ID: " +
            "980fda45-2f14-44ab-939d-46020d028ef3,<500 INTERNAL_SERVER_ERROR " +
            "Internal Server Error,System failed totally by getting the personal identification number!,[]>)",
            logCaptorRestResponseExceptionHandler.getInfoLogs().get(1));
        assertEquals("Finish method handleGlobalException(CLIENT_NAME: client1,CORRELATION_ID: " +
            "980fda45-2f14-44ab-939d-46020d028ef3)", logCaptorRestResponseExceptionHandler.getInfoLogs().get(2));
        
        logCaptorUtil.resetLogCaptor(logCaptorClientInformation);
        logCaptorUtil.resetLogCaptor(logCaptorRestResponseExceptionHandler);
    }
}
