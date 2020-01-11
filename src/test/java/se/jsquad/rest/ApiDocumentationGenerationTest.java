/*
 * Copyright 2019 JSquad AB
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

import org.apache.activemq.broker.BrokerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.env.Environment;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.yaml.snakeyaml.Yaml;
import se.jsquad.component.database.FlywayDatabaseMigration;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@TestPropertySource(locations = {"classpath:test/application.properties",
        "classpath:activemq.properties",
        "classpath:test/configuration/configuration_test.properties",
        "classpath:test/configuration/openbank_jpa.properties",
        "classpath:test/configuration/security_jpa.properties"},
        properties = {"jasypt.encryptor.password = testencryption"})
@SpringBootTest
public class ApiDocumentationGenerationTest {
    @MockBean
    private BrokerService brokerService;

    @MockBean
    private FlywayDatabaseMigration flywayDatabaseMigration;

    @Autowired
    private Environment environment;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void testGetOpenBankAPI() throws Exception {
        // When
        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.get(environment
                .getProperty("springdoc.api-docs.path") + ".yaml")).andReturn();

        // Then
        assertNotNull(mvcResult);

        Yaml yaml = new Yaml();

        Map<String, Object> yamlMap =
                yaml.load(new ByteArrayInputStream(mvcResult.getResponse().getContentAsByteArray()));

        assertTrue(((Map) yamlMap.get("paths")).containsKey("/api/client/info/{personIdentification}"));
        assertFalse(((Map) yamlMap.get("paths")).containsKey("/api/junk/info/test/{personIdentification}"));
        assertFalse(((Map) ((Map) yamlMap.get("components")).get("schemas")).containsKey("JunkApi"));


        Files.write(Paths.get("src/main/resources/schema/OpenBankAPIv" + environment.getProperty("api.version") +
                ".yaml"), mvcResult.getResponse().getContentAsByteArray());
    }
}
