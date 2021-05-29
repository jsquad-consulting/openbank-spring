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
import se.jsquad.api.health.DeepSystemStatusResponse;
import se.jsquad.api.health.HealthStatus;
import se.jsquad.api.health.ShallowSystemStatusResponse;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static se.jsquad.integration.RyukIntegration.BASE_PATH_ACTUATOR;
import static se.jsquad.integration.RyukIntegration.OPENBANK_MONITORING;
import static se.jsquad.integration.RyukIntegration.PROTOCOL_HTTP;

public class DeepAndShallowHealthCheckOkIT extends AbstractTestContainerSetup {
    @Test
    void testShallowHealthCheck() throws MalformedURLException, URISyntaxException {
        // When
        Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .get(toURI(BASE_PATH_ACTUATOR + "/shallowhealth", PROTOCOL_HTTP, OPENBANK_MONITORING)).andReturn();

        // Then
        ShallowSystemStatusResponse shallowSystemStatusResponse = gson.fromJson(response.getBody().asString(),
                ShallowSystemStatusResponse.class);

        assertEquals(200, response.getStatusCode());
        assertEquals(HealthStatus.UP, shallowSystemStatusResponse.getStatus());
    }

    @Test
    void testDeepHealthCheck() throws MalformedURLException, URISyntaxException {
        // When
        Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .get(toURI(BASE_PATH_ACTUATOR + "/deephealth", PROTOCOL_HTTP, OPENBANK_MONITORING)).andReturn();

        // Then
        DeepSystemStatusResponse deepSystemStatusResponse = gson.fromJson(response.getBody().asString(),
                DeepSystemStatusResponse.class);

        assertEquals(200, response.getStatusCode());
        assertEquals(HealthStatus.UP, deepSystemStatusResponse.getStatus());
        assertEquals(HealthStatus.UP, deepSystemStatusResponse.getService());
        assertEquals(HealthStatus.UP, deepSystemStatusResponse.getDependencies().getOpenbankDb());
        assertEquals(HealthStatus.UP, deepSystemStatusResponse.getDependencies().getSecurityDb());
    }
}