package se.jsquad.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import se.jsquad.entity.Client;
import se.jsquad.generator.EntityGenerator;
import se.jsquad.repository.ClientRepository;

import java.util.logging.Level;
import java.util.logging.Logger;

public class StartupOpenBankComponentImpl implements StartupOpenBankComponent {
    private static final Logger logger = Logger.getLogger(StartupOpenBankComponentImpl.class.getName());

    @Autowired
    @Qualifier("clientRepositoryImpl")
    private ClientRepository clientRepository;

    @Autowired
    @Qualifier("entityGeneratorImpl")
    private EntityGenerator entityGenerator;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void initiateDatabase() {
        logger.log(Level.FINE, "initiateDatabase()");

        for (Client client : entityGenerator.generateClientList()) {
            clientRepository.persistClient(client);
        }
    }

    @Override
    public void closeDatabase() {
        logger.log(Level.FINE, "closeDatabase()");
    }
}
