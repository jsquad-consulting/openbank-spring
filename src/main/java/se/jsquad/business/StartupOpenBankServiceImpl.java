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

package se.jsquad.business;

import org.apache.activemq.broker.BrokerService;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import se.jsquad.entity.Client;
import se.jsquad.entity.SystemProperty;
import se.jsquad.generator.EntityGenerator;
import se.jsquad.property.AppPropertyConfiguration;
import se.jsquad.repository.ClientRepository;
import se.jsquad.repository.SystemPropertyRepository;
import se.jsquad.thread.NumberOfLocks;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service("startupOpenBankServiceImpl")
@Qualifier("startupOpenBankService")
public class StartupOpenBankServiceImpl implements StartupOpenBankService {
    private Logger logger;

    private AppPropertyConfiguration appPropertyConfiguration;
    private ClientRepository clientRepository;
    private EntityGenerator entityGenerator;
    private SystemPropertyRepository systemPropertyRepository;
    private static final Lock lock = new ReentrantLock();
    private BrokerService brokerService;

    @Autowired
    private StartupOpenBankServiceImpl(@Qualifier("logger") Logger logger,
                                       @Qualifier("appPropertyConfiguration") AppPropertyConfiguration
                                               appPropertyConfiguration,
                                       @Qualifier("clientRepository") ClientRepository clientRepository, @Qualifier(
            "systemPropertyRepository")
                                               SystemPropertyRepository systemPropertyRepository,
                                       @Qualifier("broker") BrokerService brokerService) {
        logger.log(Level.INFO, "StartupOpenBankComponentImpl(logger: {}, appPropertyConfiguration: " +
                        "{}, clientRepository: " +
                        "{}, systemPropertyRepository: {}, brokerService: {})",
                logger, appPropertyConfiguration, clientRepository, systemPropertyRepository, brokerService);
        this.clientRepository = clientRepository;
        this.systemPropertyRepository = systemPropertyRepository;
        this.appPropertyConfiguration = appPropertyConfiguration;
        this.logger = logger;
        this.brokerService = brokerService;
    }

    @Inject
    private void setEntityGenerator(@Named("entityGeneratorImpl") EntityGenerator entityGenerator) {
        this.entityGenerator = entityGenerator;
    }

    @Override
    @PostConstruct
    @Transactional(propagation = Propagation.REQUIRED)
    public void initiateDatabase() {
        logger.log(Level.INFO, "initiateDatabase()");

        if (clientRepository.getClientByPersonIdentification("191212121212") == null) {
            for (Client client : entityGenerator.generateClientSet()) {
                clientRepository.persistClient(client);
            }

            SystemProperty systemProperty = new SystemProperty();
            systemProperty.setName(appPropertyConfiguration.getName());
            systemProperty.setValue(appPropertyConfiguration.getVersion());

            systemPropertyRepository.persistSystemProperty(systemProperty);
        }
    }

    @Override
    @PreDestroy
    public void closeDatabase() {
        logger.log(Level.INFO, "closeDatabase()");
    }


    @Override
    @Scheduled(cron = "0 0/5 * * * *")
    /**
     * Batch job that runs every five minutes to refresh the secondary cache level
     *
     * @return
     */
    public void refreshJpaCache() {
        logger.log(Level.INFO, "refreshJpaCache()");

        lock.lock();
        logger.log(Level.INFO, "Locked the batch thread.");
        NumberOfLocks.increaseNumberOfLocks();

        try {
            systemPropertyRepository.refreshSecondaryLevelCache();
        } finally {
            NumberOfLocks.decreaseNumberOfLocks();
            lock.unlock();
            logger.log(Level.INFO, "Unlocked the batch thread.");
        }
    }
}
