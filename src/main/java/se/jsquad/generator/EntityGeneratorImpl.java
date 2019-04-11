package se.jsquad.generator;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import se.jsquad.entity.Client;
import se.jsquad.entity.Person;

import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;


@Named(value = "entityGeneratorImpl")
public class EntityGeneratorImpl implements EntityGenerator {
    private Logger logger;

    @Autowired
    private EntityGeneratorImpl(@Qualifier("logger") Logger logger) {
        this.logger = logger;
        this.logger.log(Level.INFO, "EntityGeneratorImpl(logger: {})", logger);
    }

    @Override
    public List<Client> generateClientList() {
        logger.log(Level.INFO, "generateClientList()");

        List<Client> clientList = new ArrayList<>();

        Client client = new Client();
        client.setPerson(new Person());
        client.getPerson().setClient(client);
        client.getPerson().setFirstName("Mr. Spock");
        client.getPerson().setPersonIdentification("191212121212");

        clientList.add(client);

        return clientList;
    }
}
