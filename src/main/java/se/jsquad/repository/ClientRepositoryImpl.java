package se.jsquad.repository;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import se.jsquad.entity.Client;
import se.jsquad.producer.OpenBankPersistenceUnitProducer;

import javax.persistence.TypedQuery;
import java.util.List;


@Repository
public class ClientRepositoryImpl extends OpenBankPersistenceUnitProducer implements ClientRepository {
    private Logger logger;

    public ClientRepositoryImpl(Logger logger) {
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
    @Transactional(transactionManager = "transactionManagerOpenBank", propagation = Propagation.REQUIRED)
    public void persistClient(Client client) {
        logger.log(Level.INFO, "persistClient(client: {})", client);

        entityManager.persist(client);
    }
}
