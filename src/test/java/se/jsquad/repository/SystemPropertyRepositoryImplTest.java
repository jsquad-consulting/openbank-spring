package se.jsquad.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import se.jsquad.entity.SystemProperty;
import se.jsquad.producer.OpenBankPersistenceUnitProducer;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestPropertySource(locations = {"classpath:application.properties", "classpath:activemq.properties",
        "classpath:database.properties"})
@Execution(ExecutionMode.SAME_THREAD)
public class SystemPropertyRepositoryImplTest {
    @Autowired
    private SystemPropertyRepository systemPropertyRepository;

    @Autowired
    private OpenBankPersistenceUnitProducer openBankPersistenceUnitProducer;

    @Test
    public void testClearFindAndRefreshSecondaryCacheLevel() {
        // Given
        SystemProperty systemProperty = systemPropertyRepository.findAllUniqueSystemProperties().iterator().next();

        // Then
        assertTrue(openBankPersistenceUnitProducer.getEntityManager().getEntityManagerFactory().getCache()
                .contains(SystemProperty.class, systemProperty.getId()));

        // When
        systemPropertyRepository.clearSecondaryLevelCache();

        // Then
        assertFalse(openBankPersistenceUnitProducer.getEntityManager().getEntityManagerFactory().getCache().
                contains(SystemProperty.class, systemProperty.getId()));

        // When
        systemPropertyRepository.refreshSecondaryLevelCache();

        // Then
        assertTrue(openBankPersistenceUnitProducer.getEntityManager().getEntityManagerFactory().getCache()
                .contains(SystemProperty.class, systemProperty.getId()));
    }
}
