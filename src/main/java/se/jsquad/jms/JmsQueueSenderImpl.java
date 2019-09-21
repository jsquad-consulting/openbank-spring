package se.jsquad.jms;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.jms.Queue;

@Component("jmsQueueSenderImpl")
@Qualifier("jmsQueueSender")
public class JmsQueueSenderImpl implements JmsQueueSender {
    private Logger logger;
    private JmsTemplate jmsTemplate;
    private Queue queue;

    JmsQueueSenderImpl(@Qualifier("logger") Logger logger, @Qualifier("jmsTemplate") JmsTemplate jmsTemplate,
                       @Qualifier("destinationQueue") Queue queue) {
        logger.log(Level.INFO, "JmsQueueSender(logger: {}, jmsTemplate: {}, queue: {})", logger, jmsTemplate, queue);
        this.logger = logger;
        this.jmsTemplate = jmsTemplate;
        this.queue = queue;
    }

    @Override
    public void sendMessage(String message) {
        logger.log(Level.INFO, message);

        jmsTemplate.send(this.queue, session -> session.createTextMessage(message));
    }
}
