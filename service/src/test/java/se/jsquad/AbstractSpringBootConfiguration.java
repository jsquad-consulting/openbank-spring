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

package se.jsquad;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import se.jsquad.util.LogCaptorUtil;

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
public abstract class AbstractSpringBootConfiguration {
    @Autowired
    protected ObjectMapper objectMapper;
    
    @Autowired
    protected LogCaptorUtil logCaptorUtil;
}
