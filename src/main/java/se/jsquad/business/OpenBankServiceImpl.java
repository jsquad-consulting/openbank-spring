package se.jsquad.business;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import se.jsquad.entity.Client;
import se.jsquad.repository.ClientRepository;

@Service("openBankServiceImpl")
public class OpenBankServiceImpl implements OpenBankService {
    private static Logger logger = LogManager.getLogger(OpenBankServiceImpl.class.getName());

    private ClientRepository clientRepository;

    @Autowired
    private OpenBankServiceImpl(@Qualifier("clientRepositoryImpl") ClientRepository
                                          clientRepository) {
        logger.log(Level.INFO, "OpenBankControllerImpl(clientInformationService: {})",
                clientRepository);
        this.clientRepository = clientRepository;
    }

    @Override
    public Client getClientInformation(String personIdentification) {
        logger.log(Level.INFO, "getClientInformation(personIdentification: {})", "hidden");

        return clientRepository.getClientInformation(personIdentification);
    }
}
