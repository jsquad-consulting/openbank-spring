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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockserver.model.Delay;
import org.mockserver.model.Header;
import org.springframework.http.MediaType;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static se.jsquad.integration.RyukIntegration.BASE_PATH_API;
import static se.jsquad.integration.RyukIntegration.OPENBANK_SERVICE;
import static se.jsquad.integration.RyukIntegration.PROTOCOL_HTTPS;
import static se.jsquad.interceptor.RequestHeaderInterceptor.CORRELATION_ID_HEADER_NAME;
import static se.jsquad.interceptor.RequestHeaderInterceptor.X_AUTHORIZATION_HEADER_NAME;
import static se.jsquad.util.ClientTestCredentials.CLIENT_NAME;
import static se.jsquad.util.ClientTestCredentials.CLIENT_PASSWORD;

public class GetHelloWorldErrorRestControllerIT extends AbstractTestContainerSetup {
    @AfterEach
    void resetMockServerClient() {
        mockServerClient.reset();
    }
    
    @Test
    void testGetHelloWorldRestResponseServerError() throws MalformedURLException, URISyntaxException {
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
                .header(CORRELATION_ID_HEADER_NAME, "fdc29e15-9f8c-45df-aaf1-9122f3ae6089")
                .header(X_AUTHORIZATION_HEADER_NAME, Base64.getEncoder()
                    .encodeToString((CLIENT_NAME + ":" + CLIENT_PASSWORD).getBytes()))
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .get(toURI(BASE_PATH_API + "/get/hello/world", PROTOCOL_HTTPS, OPENBANK_SERVICE)).andReturn();

        // Then
        assertEquals(500, response.getStatusCode());

        assertEquals("Webclient is not available at this time.", response.getBody().print());
    }
}