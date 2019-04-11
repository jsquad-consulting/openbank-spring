package se.jsquad.repository;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import se.jsquad.entity.Client;
import se.jsquad.entity.Person;

@Repository("getClientRepositoryMock")
public class ClientRepositoryMock implements ClientRepository {
    private Logger logger;

    @Autowired
    private ClientRepositoryMock(@Qualifier("logger") Logger logger) {
        this.logger = logger;
        this.logger.log(Level.INFO, "ClientRepositoryMock(logger: {})", logger);
    }

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
