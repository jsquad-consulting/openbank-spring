package se.jsquad.generator;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import se.jsquad.entity.Client;
import se.jsquad.entity.Person;

import java.util.ArrayList;
import java.util.List;


@Service("entityGeneratorImpl")
@PropertySource("classpath:/META-INF/property/client.properties")
public class EntityGeneratorImpl implements EntityGenerator {
    private static final Logger logger = LogManager.getLogger(EntityGeneratorImpl.class.getName());

    private Environment environment;

    @Autowired
    private EntityGeneratorImpl(Environment environment) {
        logger.log(Level.INFO, "EntityGeneratorImpl(environment: {}",
                environment);
        this.environment = environment;
    }

    @Override
    public List<Client> generateClientList() {
        logger.log(Level.INFO, "generateClientList()");

        List<Client> clientList = new ArrayList<>();

        Client client = new Client();
        client.setPerson(new Person());
        client.getPerson().setClient(client);
        client.getPerson().setFirstName(environment.getProperty("person.firstName"));
        client.getPerson().setPersonIdentification(environment.getProperty("person.identification"));

        clientList.add(client);

        return clientList;
    }
}
