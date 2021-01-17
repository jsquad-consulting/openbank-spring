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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.model.Delay;
import org.mockserver.model.Header;
import se.jsquad.client.info.WorldApiResponse;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

public class GetHelloWorldOkRestControllerIT extends AbstractTestContainerSetup {
    @BeforeEach
    void setupEndpointForRestAssured() {
        setupEndpointForRestAssuredAdapterHttps();
    }
    
    @AfterEach
    void resetMockServerClient() {
        mockServerClient.reset();
    }
    
    @Test
    void testGetHelloWorldRestResponse() {
        // Given
        WorldApiResponse worldApiResponse = new WorldApiResponse();
        worldApiResponse.setMessage("Hello world");

        mockServerClient
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
        assertEquals(200, response.getStatusCode());
    
        WorldApiResponse worldApiResponseResult = gson.fromJson(response.getBody().print(),
                WorldApiResponse.class);


        assertEquals("Hello world", worldApiResponseResult.getMessage());
    }
}