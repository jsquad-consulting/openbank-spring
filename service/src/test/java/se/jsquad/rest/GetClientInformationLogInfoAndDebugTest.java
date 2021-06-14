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
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
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
import se.jsquad.component.database.FlywayDatabaseMigration;
import se.jsquad.configuration.ApplicationConfiguration;

import java.io.IOException;
import java.util.Base64;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static se.jsquad.interceptor.RequestHeaderInterceptor.CORRELATION_ID_HEADER_NAME;
import static se.jsquad.interceptor.RequestHeaderInterceptor.X_AUTHORIZATION_HEADER_NAME;
import static se.jsquad.util.ClientTestCredentials.CLIENT_NAME;
import static se.jsquad.util.ClientTestCredentials.CLIENT_PASSWORD;

public class GetClientInformationLogInfoAndDebugTest extends AbstractSpringBootConfiguration {
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
    
    @Test
    public void testGetClientInformationLoggerInfo() throws Exception {
        // Given
        final String CORRELATION_ID = "980fda45-2f14-44ab-939d-46020d028ef3";
        LogCaptor logCaptor = LogCaptor.forClass(GetClientInformationRestController.class);
        logCaptor.setLogLevelToInfo();
        
        String personIdentification = "191212121212";
    
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        // When
        MvcResult mvcResult = mockMvc.perform(get("/api/client/info/" + personIdentification)
            .header(CORRELATION_ID_HEADER_NAME, CORRELATION_ID)
            .header(X_AUTHORIZATION_HEADER_NAME, java.util.Base64.getEncoder()
                .encodeToString((CLIENT_NAME + ":" + CLIENT_PASSWORD).getBytes()))
            .accept(MediaType.APPLICATION_JSON)).andReturn();
    
        // Then
        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus(), "Failed to assert status " +
            "code 200 " + mvcResult.getResponse().getContentAsString());
    
        assertEquals(2, logCaptor.getInfoLogs().size());
        assertEquals(0, logCaptor.getDebugLogs().size());
        assertEquals(0, logCaptor.getErrorLogs().size());
        assertEquals(0, logCaptor.getTraceLogs().size());
        assertEquals(0, logCaptor.getWarnLogs().size());
        assertEquals("getClientInformation(CLIENT_NAME: client1,CORRELATION_ID: " +
            "980fda45-2f14-44ab-939d-46020d028ef3,191212121212)", logCaptor.getInfoLogs().get(0));
        assertEquals("Finish method getClientInformation(CLIENT_NAME: client1,CORRELATION_ID: " +
            "980fda45-2f14-44ab-939d-46020d028ef3)", logCaptor.getInfoLogs().get(1));
        
        logCaptorUtil.resetLogCaptor(logCaptor);
    }
    
    @Test
    public void testGetClientInformationLogDebuggerLevel() throws Exception {
        // Given
        final String CORRELATION_ID = "980fda45-2f14-44ab-939d-46020d028ef3";
        LogCaptor logCaptor = LogCaptor.forClass(GetClientInformationRestController.class);
        logCaptor.setLogLevelToDebug();
        
        String personIdentification = "191212121212";
        
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        // When
        MvcResult mvcResult = mockMvc.perform(get("/api/client/info/" + personIdentification)
            .header(CORRELATION_ID_HEADER_NAME, CORRELATION_ID)
            .header(X_AUTHORIZATION_HEADER_NAME, Base64.getEncoder()
                .encodeToString((CLIENT_NAME + ":" + CLIENT_PASSWORD).getBytes()))
            .accept(MediaType.APPLICATION_JSON)).andReturn();
        
        // Then
        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus(), "Failed to assert status " +
            "code 200 " + mvcResult.getResponse().getContentAsString());
        
        assertEquals(3, logCaptor.getInfoLogs().size());
        assertEquals(0, logCaptor.getDebugLogs().size());
        assertEquals(0, logCaptor.getErrorLogs().size());
        assertEquals(0, logCaptor.getTraceLogs().size());
        assertEquals(0, logCaptor.getWarnLogs().size());
        assertEquals("getClientInformation(CLIENT_NAME: client1,CORRELATION_ID: " +
            "980fda45-2f14-44ab-939d-46020d028ef3,191212121212)", logCaptor.getInfoLogs().get(0));
        assertTrue(logCaptor.getInfoLogs().get(1).contains("Method getClientInformation response(CLIENT_NAME: " +
                "client1,CORRELATION_ID: 980fda45-2f14-44ab-939d-46020d028ef3," +
                "<200 OK OK,se.jsquad.api.client.ClientApi"),
            "Failed to assert part of the log message " + logCaptor.getInfoLogs().get(1));
        assertEquals("Finish method getClientInformation(CLIENT_NAME: client1,CORRELATION_ID: " +
            "980fda45-2f14-44ab-939d-46020d028ef3)", logCaptor.getInfoLogs().get(2));
        
        logCaptorUtil.resetLogCaptor(logCaptor);
    }
}
