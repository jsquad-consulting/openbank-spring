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

package se.jsquad.business;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.AnnotationConfigWebContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;
import se.jsquad.configuration.ApplicationConfiguration;
import se.jsquad.entity.SystemProperty;
import se.jsquad.producer.OpenBankPersistenceUnitProducer;
import se.jsquad.property.AppPropertyConfiguration;
import se.jsquad.repository.SystemPropertyRepository;
import se.jsquad.thread.NumberOfLocks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = ApplicationConfiguration.class, loader = AnnotationConfigWebContextLoader.class)
@Execution(ExecutionMode.SAME_THREAD)
public class StartupOpenBankServiceImplTest {
    @Autowired
    StartupOpenBankService startupOpenBankService;

    @Autowired
    SystemPropertyRepository systemPropertyRepository;

    @Autowired
    AppPropertyConfiguration appPropertyConfiguration;

    @Autowired
    OpenBankPersistenceUnitProducer openBankPersistenceUnitProducer;

    private boolean runningThreads = true;

    @Test
    public void testSystemPropertyCacheIsPopulated() {
        // When
        List<SystemProperty> systemPropertyList = systemPropertyRepository.findAllUniqueSystemProperties();

        // Then
        assertEquals(1, systemPropertyList.size());

        SystemProperty systemProperty = systemPropertyList.iterator().next();

        assertTrue(openBankPersistenceUnitProducer.getEntityManager().getEntityManagerFactory().getCache()
                .contains(SystemProperty.class, systemProperty.getId()));

        assertEquals(appPropertyConfiguration.getName(), systemProperty.getName());
        assertEquals(appPropertyConfiguration.getVersion(), systemProperty.getValue());
    }

    @Test
    public void testConcurrentRefreshTheSecondaryLevelCache() {
        List<Integer> numberOfLockList = new ArrayList<>();
        var executorService = Executors.newFixedThreadPool(1001);

        executorService.execute(() -> {
            while (runningThreads) {
                numberOfLockList.add(NumberOfLocks.getCountNumberOfLocks());
            }
        });


        for (int i = 0; i < 1000; ++i) {
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
