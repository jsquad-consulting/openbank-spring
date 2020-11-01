/*
 * Copyright 2019 JSquad AB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package se.jsquad.business;

import org.apache.logging.log4j.Logger;
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
import java.util.concurrent.Future;

@Service
@Transactional(transactionManager = "transactionManagerOpenBank", propagation = Propagation.REQUIRED)
public class OpenBankServiceImpl implements OpenBankService {
    private Logger logger;

    private ClientRepository clientRepository;
    private ClientAdapter clientAdapter;
    private SlowMockBatch slowMockBatch;

    public OpenBankServiceImpl(Logger logger, ClientRepository clientRepository, SlowMockBatch slowMockBatch) {
        this.clientRepository = clientRepository;
        this.slowMockBatch = slowMockBatch;
        this.logger = logger;
    }

    @Inject
    private void setClientAdapter(ClientAdapter clientAdapter) {
        this.clientAdapter = clientAdapter;
    }

    @Override
    public ClientApi getClientInformationByPersonIdentification(String personIdentification) {
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
        return new AsyncResult<>(slowMockBatch.startBatch());
    }
}
