package se.jsquad.property;

import org.apache.activemq.broker.BrokerService;
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
        "classpath:configuration/configuration_test.yaml",
        "classpath:configuration/openbank_jpa.yaml",
        "classpath:configuration/security_jpa.yaml"}, properties = {"jasypt.encryptor.password = testencryption"})
public class AppPropertyImplTest {
    @MockBean
    private BrokerService brokerService;

    @MockBean
    private FlywayDatabaseMigration flywayDatabaseMigration;

    @Autowired
    private AppPropertyConfiguration appPropertyConfiguration;

    @Test
    public void testAppProperty() {
        // Then
        assertEquals("OpenBank", appPropertyConfiguration.getName());
        assertEquals("1.0.0", appPropertyConfiguration.getVersion());
    }
}
