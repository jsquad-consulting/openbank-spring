package se.jsquad.business;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import se.jsquad.adapter.ClientAdapter;
import se.jsquad.batch.SlowMockBatch;
import se.jsquad.batch.status.BatchStatus;
import se.jsquad.client.info.ClientApi;
import se.jsquad.entity.Client;
import se.jsquad.repository.ClientRepository;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.concurrent.Future;

@Service("openBankServiceImpl")
@Qualifier("openBankService")
@Transactional(propagation = Propagation.REQUIRED)
public class OpenBankServiceImpl implements OpenBankService {
    private Logger logger;

    private ClientRepository clientRepository;
    private ClientAdapter clientAdapter;
    private SlowMockBatch slowMockBatch;

    @Autowired
    private OpenBankServiceImpl(@Qualifier("logger") Logger logger, @Qualifier("clientRepository") ClientRepository
            clientRepository, @Qualifier("slowMockBatch") SlowMockBatch slowMockBatch) {
        logger.log(Level.INFO, "OpenBankControllerImpl(logger: {}, clientInformationService: {}, slowMockBatch: {})",
                logger, clientRepository, slowMockBatch);
        this.clientRepository = clientRepository;
        this.slowMockBatch = slowMockBatch;
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

    @Async
    @Override
    public Future<BatchStatus> startSlowBatch() throws InterruptedException {
        logger.log(Level.INFO, "startSlowBatch()");

        return new AsyncResult<>(slowMockBatch.startBatch());
    }
}
