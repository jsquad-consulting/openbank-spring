package se.jsquad.repository;

import org.springframework.stereotype.Repository;
import se.jsquad.entity.Client;
import se.jsquad.entity.Person;

import java.util.logging.Level;
import java.util.logging.Logger;

@Repository("getClientRepositoryMock")
public class ClientRepositoryMock implements ClientRepository {
    private static final Logger logger = Logger.getLogger(ClientRepositoryMock.class.getName());

    @Override
    public Client getClientInformation(String personIdentification) {
        logger.log(Level.FINE, "getClientInformation(personIdentification: {0})",
                new Object[]{"hidden"});

        Client client = new Client();
        client.setPerson(new Person());

        client.getPerson().setFirstName("Mr. Spock");

        return client;
    }

    @Override
    public void persistClient(Client client) {
        logger.log(Level.FINE, "persistClient(client: {0})", new Object[]{client});
    }
}
