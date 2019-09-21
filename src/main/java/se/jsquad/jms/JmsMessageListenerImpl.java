package se.jsquad.jms;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.jms.Message;
import javax.jms.MessageListener;

@Component("jmsMessageListenerImpl")
@Qualifier("jmsMessageListener")
public class JmsMessageListenerImpl implements MessageListener {
    private Logger logger;

    public JmsMessageListenerImpl(@Qualifier("logger") Logger logger) {
        logger.log(Level.INFO, "JmsQueueSender(logger: {}", logger);

        this.logger = logger;
    }

    @Override
    public void onMessage(Message message) {
        logger.log(Level.INFO, "onMessage(message: {})", message);
    }
}
