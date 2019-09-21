package se.jsquad.jms;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import se.jsquad.configuration.ApplicationConfiguration;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;


@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ApplicationConfiguration.class, loader = AnnotationConfigContextLoader.class)
public class JmsQueueSenderImplTest {
    @Autowired
    private JmsQueueSender jmsQueueSender;

    @Test
    public void testJmsQueueSender() {
        // When
        assertDoesNotThrow(() -> jmsQueueSender.sendMessage("Hello destination queue"));
    }
}