package se.jsquad.business;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import se.jsquad.entity.Client;
import se.jsquad.generator.EntityGenerator;
import se.jsquad.repository.ClientRepository;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

@Service("startupOpenBankServiceImpl")
public class StartupOpenBankServiceImpl implements StartupOpenBankService {
    private Logger logger;

    private ClientRepository clientRepository;
    private EntityGenerator entityGenerator;

    @Autowired
    private StartupOpenBankServiceImpl(@Qualifier("logger") Logger logger,
                                       @Qualifier("clientRepositoryImpl") ClientRepository clientRepository) {
        this.logger = logger;
        this.logger.log(Level.INFO, "StartupOpenBankComponentImpl(logger: {}, clientRepository: {})",
                logger, clientRepository);
        this.clientRepository = clientRepository;
    }

    @Inject
    private void setEntityGenerator(@Named("entityGeneratorImpl") EntityGenerator entityGenerator) {
        this.entityGenerator = entityGenerator;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    @PostConstruct
    public void initiateDatabase() {
        logger.log(Level.INFO, "initiateDatabase()");

        if (clientRepository.getClientInformation("191212121212") == null) {
            for (Client client : entityGenerator.generateClientList()) {
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
