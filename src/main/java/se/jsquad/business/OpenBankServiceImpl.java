package se.jsquad.business;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import se.jsquad.entity.Client;
import se.jsquad.repository.ClientRepository;

@Service("openBankServiceImpl")
@Qualifier("openBankService")
public class OpenBankServiceImpl implements OpenBankService {
    private Logger logger;

    private ClientRepository clientRepository;

    @Autowired
    private OpenBankServiceImpl(@Qualifier("logger") Logger logger, @Qualifier("clientRepository") ClientRepository
                                          clientRepository) {
        logger.log(Level.INFO, "OpenBankControllerImpl(logger: {}, clientInformationService: {})",
                logger, clientRepository);
        this.clientRepository = clientRepository;
        this.logger = logger;
    }

    @Override
    public Client getClientInformationByPersonIdentification(String personIdentification) {
        logger.log(Level.INFO, "getClientByPersonIdentification(personIdentification: {})", "hidden");

        return clientRepository.getClientByPersonIdentification(personIdentification);
    }
}
