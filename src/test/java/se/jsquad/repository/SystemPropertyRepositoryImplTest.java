package se.jsquad.repository;

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

import javax.persistence.EntityManager;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestPropertySource(locations = {"classpath:application.properties", "classpath:activemq.properties",
        "classpath:test/configuration/configuration_test.yaml",
        "classpath:test/configuration/openbank_jpa.yaml",
        "classpath:test/configuration/security_jpa.yaml"},
        properties = {"jasypt.encryptor.password = testencryption"})
@Execution(ExecutionMode.SAME_THREAD)
public class SystemPropertyRepositoryImplTest {
    @MockBean
    private BrokerService brokerService;

    @MockBean
    private FlywayDatabaseMigration flywayDatabaseMigration;

    @Autowired
    private SystemPropertyRepository systemPropertyRepository;

    @Autowired
    private OpenBankPersistenceUnitProducer openBankPersistenceUnitProducer;

    private EntityManager entityManager;

    @BeforeEach
    void enableAccessToEntityManager() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = OpenBankPersistenceUnitProducer.class.getDeclaredMethod("getEntityManager");
        method.setAccessible(true);

        entityManager = (EntityManager) method.invoke(openBankPersistenceUnitProducer);
    }

    @Test
    public void testClearFindAndRefreshSecondaryCacheLevel() {
        // Given
        SystemProperty systemProperty = systemPropertyRepository.findAllUniqueSystemProperties().iterator().next();

        // Then
        assertTrue(entityManager.getEntityManagerFactory().getCache()
                .contains(SystemProperty.class, systemProperty.getId()));

        // When
        systemPropertyRepository.clearSecondaryLevelCache();

        // Then
        assertFalse(entityManager.getEntityManagerFactory().getCache().
                contains(SystemProperty.class, systemProperty.getId()));

        // When
        systemPropertyRepository.refreshSecondaryLevelCache();

        // Then
        assertTrue(entityManager.getEntityManagerFactory().getCache()
                .contains(SystemProperty.class, systemProperty.getId()));
    }
}
