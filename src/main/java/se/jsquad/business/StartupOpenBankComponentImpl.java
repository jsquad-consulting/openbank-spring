package se.jsquad.business;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import se.jsquad.entity.Client;
import se.jsquad.generator.EntityGenerator;
import se.jsquad.repository.ClientRepository;

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
    public void initiateDatabase() {
        logger.log(Level.INFO, "initiateDatabase()");

        for (Client client : entityGenerator.generateClientList()) {
            clientRepository.persistClient(client);
        }
    }

    @Override
    public void closeDatabase() {
        logger.log(Level.INFO, "closeDatabase()");
    }
}
