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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MonitorPrometheusUpStatusIT extends AbstractTestContainerSetup {
    @BeforeEach
    void setupEndpointForRestAssured() {
        setupEndPointRestAssured(PROTOCOL_HTTP, SERVICE_NAME, MONITORING_PORT, BASE_PATH_ACTUATOR);
    }
    
    @Test
    void testDeepHealthMetricsOk() {
        // When
        Response response = RestAssured
                .given()
                .contentType(ContentType.ANY)
                .accept(ContentType.ANY)
                .when()
                .get(URI.create("/prometheus")).andReturn();

        // Then
        assertEquals(200, response.getStatusCode());

        assertTrue(response.getBody().asString().contains("deep_health{status=\"up\",} 1.0"));
        assertTrue(response.getBody().asString().contains("deep_health_service{status=\"up\",} 1.0"));
        assertTrue(response.getBody().asString().contains("deep_health_openbank_database{status=\"up\",} 1.0"));
        assertTrue(response.getBody().asString().contains("deep_health_security_database{status=\"up\",} 1.0"));
    }
}