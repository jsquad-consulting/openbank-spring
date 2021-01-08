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

package se.jsquad.component.jpa;

import org.apache.activemq.broker.BrokerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import se.jsquad.component.database.FlywayDatabaseMigration;
import se.jsquad.configuration.ApplicationConfiguration;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestPropertySource(locations = {"classpath:test/application.properties",
        "classpath:activemq.properties",
        "classpath:test/configuration/configuration_test.properties",
        "classpath:test/configuration/openbank_jpa.properties",
        "classpath:test/configuration/security_jpa.properties"},
        properties = {"jasypt.encryptor.password = testencryption"})
@Execution(ExecutionMode.SAME_THREAD)
public class JpaConfigurationH2Test {
    @Configuration
    @Import(ApplicationConfiguration.class)
    // Just add the existing beans here and change the implementation needed
    public static class TestConfig {}

    @MockBean
    private BrokerService brokerService;

    @MockBean
    private FlywayDatabaseMigration flywayDatabaseMigration;

    @Autowired
    private OpenBankJpaConfiguration openBankJpaConfiguration;

    @Autowired
    private SecurityJpaConfiguration securityJpaConfiguration;

    @Test
    public void testOpenBankJpaConfiguration() {
        // Then
        assertEquals("org.hibernate.dialect.H2Dialect", openBankJpaConfiguration.getDatabasePlatform());
        assertEquals("validate", openBankJpaConfiguration.getEntityValidation());
        assertEquals("drop-and-create", openBankJpaConfiguration.getDatabaseAction());
        assertEquals("true", openBankJpaConfiguration.getSecondaryLevelCache());
        assertEquals("org.hibernate.cache.ehcache.EhCacheRegionFactory",
                openBankJpaConfiguration.getCacheRegionFactory());
    }

    @Test
    public void testsecurityJpaConfiguration() {
        // Then
        assertEquals("org.hibernate.dialect.H2Dialect", securityJpaConfiguration.getDatabasePlatform());
        assertEquals("validate", securityJpaConfiguration.getEntityValidation());
        assertEquals("drop-and-create", securityJpaConfiguration.getDatabaseAction());
        assertEquals("true", securityJpaConfiguration.getSecondaryLevelCache());
        assertEquals("org.hibernate.cache.ehcache.EhCacheRegionFactory",
                securityJpaConfiguration.getCacheRegionFactory());
    }
}