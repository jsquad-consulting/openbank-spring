package se.jsquad.business;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
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
@ContextConfiguration({"classpath:META-INF/applicationContext.xml"})
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
