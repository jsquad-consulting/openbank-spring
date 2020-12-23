/*
 * Copyright 2020 JSquad AB
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

package se.jsquad.business;

import javax.persistence.EntityManager;
import org.apache.activemq.broker.BrokerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import se.jsquad.component.database.FlywayDatabaseMigration;
import se.jsquad.entity.SystemProperty;
import se.jsquad.producer.OpenBankPersistenceUnitProducer;
import se.jsquad.property.AppPropertyConfiguration;
import se.jsquad.repository.SystemPropertyRepository;
import se.jsquad.thread.NumberOfLocks;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestPropertySource(locations = {"classpath:test/application.properties",
        "classpath:activemq.properties",
        "classpath:test/configuration/configuration_test.properties",
        "classpath:test/configuration/openbank_jpa.properties",
        "classpath:test/configuration/security_jpa.properties"},
        properties = {"jasypt.encryptor.password = testencryption"})
@Execution(ExecutionMode.SAME_THREAD)
public class StartupOpenBankServiceImplTest {
    @MockBean
    private BrokerService brokerService;

    @MockBean
    private FlywayDatabaseMigration flywayDatabaseMigration;

    @Autowired
    StartupOpenBankService startupOpenBankService;

    @Autowired
    SystemPropertyRepository systemPropertyRepository;

    @Autowired
    AppPropertyConfiguration appPropertyConfiguration;

    @Autowired
    OpenBankPersistenceUnitProducer openBankPersistenceUnitProducer;

    private boolean runningThreads = true;

    private EntityManager entityManager;

    @BeforeEach
    void enableAccessToEntityManager() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = OpenBankPersistenceUnitProducer.class.getDeclaredMethod("getEntityManager");
        method.setAccessible(true);

        entityManager = (EntityManager) method.invoke(openBankPersistenceUnitProducer);
    }

    @Test
    public void testSystemPropertyCacheIsPopulated() {
        // When
        List<SystemProperty> systemPropertyList = systemPropertyRepository.findAllUniqueSystemProperties();

        // Then
        assertEquals(1, systemPropertyList.size());

        SystemProperty systemProperty = systemPropertyList.iterator().next();

        assertTrue(entityManager.getEntityManagerFactory().getCache()
                .contains(SystemProperty.class, systemProperty.getId()));

        assertEquals(appPropertyConfiguration.getName(), systemProperty.getName());
        assertEquals(appPropertyConfiguration.getVersion(), systemProperty.getValue());
    }

    @Test
    public void testConcurrentRefreshTheSecondaryLevelCache() {
        List<Integer> numberOfLockList = new ArrayList<>();
        var executorService = Executors.newFixedThreadPool(25);

        executorService.execute(() -> {
            while (runningThreads) {
                numberOfLockList.add(NumberOfLocks.getCountNumberOfLocks());
            }
        });


        for (int i = 0; i < 25; ++i) {
            executorService.execute(() -> startupOpenBankService.refreshJpaCache());
        }
        runningThreads = false;
        executorService.shutdown();

        while (!executorService.isTerminated()) {
            // Loops until all threads are executed.
        }

        assertTrue(numberOfLockList.stream().noneMatch(n -> n.intValue() < 0 || n.intValue() > 1));
    }
}
