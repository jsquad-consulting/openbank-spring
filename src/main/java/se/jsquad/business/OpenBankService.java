package se.jsquad.business;

import se.jsquad.batch.status.BatchStatus;
import se.jsquad.client.info.ClientApi;

import java.util.concurrent.Future;

public interface OpenBankService {
    ClientApi getClientInformationByPersonIdentification(String personIdentification);

    Future<BatchStatus> startSlowBatch() throws InterruptedException;
}
