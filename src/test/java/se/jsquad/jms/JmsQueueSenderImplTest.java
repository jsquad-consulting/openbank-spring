package se.jsquad.jms;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import se.jsquad.component.database.FlywayDatabaseMigration;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;


@ExtendWith(SpringExtension.class)
@TestPropertySource(locations = {"classpath:application.properties", "classpath:activemq.properties",
        "classpath:configuration/configuration_test.yaml", "classpath:configuration/openbank_persistence.properties",
        "classpath:configuration/security_persistence.properties"})
@SpringBootTest
public class JmsQueueSenderImplTest {
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