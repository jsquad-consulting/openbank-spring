package se.jsquad.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import se.jsquad.entity.Client;
import se.jsquad.repository.ClientRepository;

import java.util.logging.Level;
import java.util.logging.Logger;

@Component("openBankComponent")
public class OpenBankComponentImpl implements OpenBankComponent {
    private static Logger logger = Logger.getLogger(OpenBankComponentImpl.class.getName());

    private ClientRepository clientRepository;

    @Autowired
    private OpenBankComponentImpl(@Qualifier("clientRepositoryImpl") ClientRepository
                                          clientRepository) {
        logger.log(Level.FINE, "OpenBankControllerImpl(clientInformationService: {0})",
                new Object[]{clientRepository});
        this.clientRepository = clientRepository;
    }

    @Override
    public Client getClientInformation(String personIdentification) {
        logger.log(Level.FINE, "getClientInformation(personIdentification: {0})", new Object[]{"hidden"});

        return clientRepository.getClientInformation(personIdentification);
    }
}
