package se.jsquad.component.jpa;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import se.jsquad.component.database.FlywayDatabaseMigration;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestPropertySource(locations = {"classpath:application.properties", "classpath:activemq.properties",
        "classpath:test/configuration/configuration_test.yaml",
        "classpath:test/configuration/openbank_jpa.yaml",
        "classpath:test/configuration/security_jpa.yaml"},
        properties = {"jasypt.encryptor.password = testencryption"})
public class JpaConfigurationH2Test {
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