package se.jsquad.jms;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.jms.Message;
import javax.jms.MessageListener;

@Component
public class JmsMessageListenerImpl implements MessageListener {
    private Logger logger;

    public JmsMessageListenerImpl(Logger logger) {
        logger.log(Level.INFO, "JmsQueueSender(logger: {}", logger);

        this.logger = logger;
    }

    @Override
    public void onMessage(Message message) {
        logger.log(Level.INFO, "onMessage(message: {})", message);
    }
}
