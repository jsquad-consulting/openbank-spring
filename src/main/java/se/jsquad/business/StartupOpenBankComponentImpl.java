package se.jsquad.business;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import se.jsquad.entity.Client;
import se.jsquad.generator.EntityGenerator;
import se.jsquad.repository.ClientRepository;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component("startupOpenBankComponentImpl")
public class StartupOpenBankComponentImpl implements StartupOpenBankComponent {
    private static final Logger logger = LogManager.getLogger(StartupOpenBankComponentImpl.class.getName());

    @Autowired
    @Qualifier("clientRepositoryImpl")
    private ClientRepository clientRepository;

    @Autowired
    @Qualifier("entityGeneratorImpl")
    private EntityGenerator entityGenerator;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    @PostConstruct
    public void initiateDatabase() {
        logger.log(Level.INFO, "initiateDatabase()");

        for (Client client : entityGenerator.generateClientList()) {
            clientRepository.persistClient(client);
        }
    }

    @Override
    @PreDestroy
    public void closeDatabase() {
        logger.log(Level.INFO, "closeDatabase()");
    }
}
