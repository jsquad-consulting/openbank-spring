package se.jsquad.repository;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import se.jsquad.entity.Client;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;


@Repository("clientRepositoryImpl")
public class ClientRepositoryImpl implements ClientRepository {
    private static final Logger logger = LogManager.getLogger(ClientRepositoryImpl.class.getName());

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Client getClientInformation(String personIdentification) {
        logger.log(Level.INFO, "getClientInformation(personIdentification: {})", "hidden");

        TypedQuery<Client> query = entityManager.createNamedQuery(Client.PERSON_IDENTIFICATION, Client.class);
        query.setParameter(Client.PERSON_IDENTIFICSTION_PARAM, personIdentification);

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
