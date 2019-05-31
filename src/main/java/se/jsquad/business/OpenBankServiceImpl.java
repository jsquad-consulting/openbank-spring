package se.jsquad.business;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import se.jsquad.adapter.ClientAdapter;
import se.jsquad.client.info.ClientApi;
import se.jsquad.entity.Client;
import se.jsquad.repository.ClientRepository;

import javax.inject.Inject;
import javax.inject.Named;

@Service("openBankServiceImpl")
@Qualifier("openBankService")
@Transactional(propagation = Propagation.REQUIRED)
public class OpenBankServiceImpl implements OpenBankService {
    private Logger logger;

    private ClientRepository clientRepository;
    private ClientAdapter clientAdapter;

    @Autowired
    private OpenBankServiceImpl(@Qualifier("logger") Logger logger, @Qualifier("clientRepository") ClientRepository
            clientRepository) {
        logger.log(Level.INFO, "OpenBankControllerImpl(logger: {}, clientInformationService: {})",
                logger, clientRepository);
        this.clientRepository = clientRepository;
        this.logger = logger;
    }

    @Inject
    private void setClientAdapter(@Named("clientAdapterImpl") ClientAdapter clientAdapter) {
        this.clientAdapter = clientAdapter;
    }

    @Override
    public ClientApi getClientInformationByPersonIdentification(String personIdentification) {
        logger.log(Level.INFO, "getClientByPersonIdentification(personIdentification: {})", "hidden");

        Client client = clientRepository.getClientByPersonIdentification(personIdentification);

        if (client == null) {
            return null;
        } else {
            return clientAdapter.translateClientToClientApi(client);
        }
    }
}
