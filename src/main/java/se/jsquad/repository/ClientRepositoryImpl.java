package se.jsquad.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import se.jsquad.entity.Client;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Repository("clientRepositoryImpl")
public class ClientRepositoryImpl implements ClientRepository {
    private static final Logger logger = Logger.getLogger(ClientRepositoryImpl.class.getName());

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Client getClientInformation(String personIdentification) {
        logger.log(Level.FINE, "getClientInformation(personIdentification: {0})", new Object[]{"hidden"});

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
        logger.log(Level.FINE, "persistClient(client: {0})", new Object[]{client});

        entityManager.persist(client);
    }
}
