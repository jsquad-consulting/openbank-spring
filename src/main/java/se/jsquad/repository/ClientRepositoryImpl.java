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

package se.jsquad.repository;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import se.jsquad.entity.Client;
import se.jsquad.producer.OpenBankPersistenceUnitProducer;

import javax.persistence.TypedQuery;
import java.util.List;


@Repository("clientRepositoryImpl")
@Qualifier("clientRepository")
public class ClientRepositoryImpl extends OpenBankPersistenceUnitProducer implements ClientRepository {
    private Logger logger;

    @Autowired
    private ClientRepositoryImpl(@Qualifier("logger") Logger logger) {
        logger.log(Level.INFO, "ClientRepositoryImpl(logger: {})", logger);
        this.logger = logger;
    }

    @Override
    public Client getClientByPersonIdentification(String personIdentification) {
        logger.log(Level.INFO, "getClientByPersonIdentification(personIdentification: {})", "hidden");

        TypedQuery<Client> query = entityManager.createNamedQuery(Client.PERSON_IDENTIFICATION, Client.class);
        query.setParameter(Client.PARAM_PERSON_IDENTIFICATION, personIdentification);

        List<Client> clientList = query.getResultList();

        if (clientList == null || clientList.isEmpty()) {
            return null;
        } else {
            return clientList.get(0);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void persistClient(Client client) {
        logger.log(Level.INFO, "persistClient(client: {})", client);

        entityManager.persist(client);
    }
}
