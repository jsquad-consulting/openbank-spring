package se.jsquad.jms;

import org.apache.activemq.broker.BrokerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.AnnotationConfigWebContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;
import se.jsquad.configuration.ApplicationConfiguration;

import javax.jms.MessageListener;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = ApplicationConfiguration.class, loader = AnnotationConfigWebContextLoader.class)
public class JmsContainerIntegrationTest {
    @Autowired
    private JmsQueueSender jmsQueueSender;

    @Autowired
    @Qualifier("jmsMessageListener")
    private MessageListener messageListener;

    @Autowired
    @Qualifier("broker")
    private BrokerService brokerService;


    @Test
    public void testContainerIntegrationTest() {
        // When
        jmsQueueSender.sendMessage("Hello destination queue");
    }
}
