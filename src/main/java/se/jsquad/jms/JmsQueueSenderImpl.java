package se.jsquad.jms;

import org.apache.logging.log4j.Logger;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.jms.Queue;

@Component
public class JmsQueueSenderImpl implements JmsQueueSender {
    private Logger logger;
    private JmsTemplate jmsTemplate;
    private Queue queue;

    JmsQueueSenderImpl(Logger logger, JmsTemplate jmsTemplate, Queue queue) {
        this.logger = logger;
        this.jmsTemplate = jmsTemplate;
        this.queue = queue;
    }

    @Override
    public void sendMessage(String message) {
        jmsTemplate.send(this.queue, session -> session.createTextMessage(message));
    }
}
