package se.jsquad.jms;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;


@ExtendWith(SpringExtension.class)
@TestPropertySource(locations = {"classpath:application.properties", "classpath:activemq.properties",
        "classpath:openbank_database.properties", "classpath:security_database.properties"})
@SpringBootTest
public class JmsQueueSenderImplTest {
    @Autowired
    private JmsQueueSender jmsQueueSender;

    @Test
    public void testJmsQueueSender() {
        // When
        assertDoesNotThrow(() -> jmsQueueSender.sendMessage("Hello destination queue"));
    }
}