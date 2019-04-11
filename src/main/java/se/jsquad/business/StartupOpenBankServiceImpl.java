package se.jsquad.business;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import se.jsquad.entity.Client;
import se.jsquad.generator.EntityGeneratorComponent;
import se.jsquad.repository.ClientRepository;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Service("startupOpenBankServiceImpl")
public class StartupOpenBankServiceImpl implements StartupOpenBankService {
    private static final Logger logger = LogManager.getLogger(StartupOpenBankServiceImpl.class.getName());

    private ClientRepository clientRepository;
    private EntityGeneratorComponent entityGeneratorComponent;

    @Autowired
    private StartupOpenBankServiceImpl(@Qualifier("clientRepositoryImpl") ClientRepository clientRepository,
                                       @Qualifier("entityGeneratorComponentImpl") EntityGeneratorComponent
                                               entityGeneratorComponent) {
        logger.log(Level.INFO, "StartupOpenBankComponentImpl(clientRepository: {}, entityGenerator: {}",
                clientRepository, entityGeneratorComponent);
        this.clientRepository = clientRepository;
        this.entityGeneratorComponent = entityGeneratorComponent;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    @PostConstruct
    public void initiateDatabase() {
        logger.log(Level.INFO, "initiateDatabase()");

        if (clientRepository.getClientInformation("191212121212") == null) {
            for (Client client : entityGeneratorComponent.generateClientList()) {
                clientRepository.persistClient(client);
            }
        }
    }

    @Override
    @PreDestroy
    public void closeDatabase() {
        logger.log(Level.INFO, "closeDatabase()");
    }
}
