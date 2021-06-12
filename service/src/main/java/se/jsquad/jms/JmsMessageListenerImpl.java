/*
 * Copyright 2021 JSquad AB
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

import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import javax.jms.Message;
import javax.jms.MessageListener;

@Component
public class JmsMessageListenerImpl implements MessageListener {
    private Logger logger;

    public JmsMessageListenerImpl(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void onMessage(Message message) {
        logger.debug("onMessage(message: {})", message);
    }
}
