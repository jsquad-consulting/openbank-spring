/*
 * Copyright 2019 JSquad AB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package se.jsquad.jms;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.TextMessage;

@Component("jmsMessageListenerImpl")
@Qualifier("jmsMessageListener")
public class JmsMessageListenerImpl implements MessageListener {
    private Logger logger;
    private JmsTemplate jmsTemplate;
    private Queue queue;

    @Autowired
    public JmsMessageListenerImpl(@Qualifier("logger") Logger logger,
                                  @Qualifier("jmsTemplate") JmsTemplate jmsTemplate,
                                  @Qualifier("destinationQueue") Queue queue) {
        logger.log(Level.INFO, "JmsQueueSender(logger: {}, jmsTemplate: {}, queue: {}", logger, jmsTemplate, queue);

        this.logger = logger;
        this.jmsTemplate = jmsTemplate;
        this.queue = queue;
    }

    @Override
    public void onMessage(Message message) {
        logger.log(Level.INFO, "onMessage(message: {})", (TextMessage) message);
    }
}
