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

package se.jsquad.batch;

import org.apache.activemq.broker.BrokerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import se.jsquad.AbstractSpringBootConfiguration;
import se.jsquad.api.batch.BatchStatus;
import se.jsquad.api.batch.Status;
import se.jsquad.component.database.FlywayDatabaseMigration;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SlowMockBatchImplTest extends AbstractSpringBootConfiguration {
    @MockBean
    private BrokerService brokerService;

    @MockBean
    private FlywayDatabaseMigration flywayDatabaseMigration;

    @Autowired
    private SlowMockBatch slowMockBatch;

    @Test
    public void testStartBatch() throws IllegalAccessException, NoSuchFieldException, InterruptedException {
        // Given
        int seconds = 0;

        Field field = SlowMockBatchImpl.class.getDeclaredField("sleepTime");
        field.setAccessible(true);

        field.set((SlowMockBatchImpl) slowMockBatch, seconds);

        // When
        BatchStatus batchStatus = slowMockBatch.startBatch();

        // Then
        assertEquals(Status.SUCCESS, batchStatus.getStatus());
        assertEquals("Batch job went just fine.", batchStatus.getMessage());
    }
}
