package se.jsquad.repository;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;
import se.jsquad.entity.Client;
import se.jsquad.entity.Person;

@Repository("getClientRepositoryMock")
public class ClientRepositoryMock implements ClientRepository {
    private static final Logger logger = LogManager.getLogger(ClientRepositoryMock.class.getName());

    @Override
    public Client getClientInformation(String personIdentification) {
        logger.log(Level.INFO, "getClientInformation(personIdentification: {})",
                "hidden");

        Client client = new Client();
        client.setPerson(new Person());

        client.getPerson().setFirstName("Mr. Spock");

        return client;
    }

    @Override
    public void persistClient(Client client) {
        logger.log(Level.INFO, "persistClient(client: {})", client);
    }
}
