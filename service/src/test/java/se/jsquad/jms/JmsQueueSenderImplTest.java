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

package se.jsquad.jms;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import se.jsquad.AbstractSpringBootConfiguration;
import se.jsquad.component.database.FlywayDatabaseMigration;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class JmsQueueSenderImplTest extends AbstractSpringBootConfiguration {
    @MockBean
    private FlywayDatabaseMigration flywayDatabaseMigration;

    @Autowired
    private JmsQueueSender jmsQueueSender;

    @Test
    public void testJmsQueueSender() {
        // When
        assertDoesNotThrow(() -> jmsQueueSender.sendMessage("Hello destination queue"));
    }
}