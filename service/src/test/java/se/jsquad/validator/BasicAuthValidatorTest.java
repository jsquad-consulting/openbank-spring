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

package se.jsquad.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import se.jsquad.AbstractSpringBootConfiguration;
import se.jsquad.component.database.FlywayDatabaseMigration;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BasicAuthValidatorTest extends AbstractSpringBootConfiguration {
    @Autowired
    private BasicAuthValidator basicAuthValidator;
    
    @MockBean
    private FlywayDatabaseMigration flywayDatabaseMigration;
    
    @Test
    void testBasicAuthMapContent() {
        // Given
        final Map<String, String> basicAuthMap = basicAuthValidator.getBasicAuthMap();
        
        // When and then
        assertTrue(basicAuthMap.containsKey("client1.token"), "Failed to assert map " + basicAuthMap.toString());
        
        assertEquals("password1,password2", basicAuthMap.get("client1.token"), "Failed to assert value "
            + basicAuthMap.get("client1"));
    }
    
    @ParameterizedTest
    @CsvSource({"client1,koko"})
    void testValidBasicAuthClientNameAndInValidPassword(String clientName, String password) {
        // When & then
        assertFalse(basicAuthValidator.doesBasicAuthNameAndPasswordExist(clientName, password));
    }
    
    @Test
    void testInvalidClientBasicAuthNameAndInvalidPassword() {
        // Given
        final String clientName = null;
        final String password = null;
        
        // When & then
        assertFalse(basicAuthValidator.doesBasicAuthNameAndPasswordExist(clientName, password));
    }
    
    @ParameterizedTest
    @CsvSource({"client1,password1", "client1,password2"})
    void testValidClientNameAndValidPassword(String clientName, String password) {
        // When & then
        assertTrue(basicAuthValidator.doesBasicAuthNameAndPasswordExist(clientName, password));
    }
    
    @ParameterizedTest
    @CsvSource({"donald,password1", "duck,password2"})
    void testInValidClientNameAndValidExistingPasswordsInTheMap(String clientName, String password) {
        // When & then
        assertFalse(basicAuthValidator.doesBasicAuthNameAndPasswordExist(clientName, password));
    }
}