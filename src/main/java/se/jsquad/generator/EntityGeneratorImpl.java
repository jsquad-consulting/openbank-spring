package se.jsquad.generator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import se.jsquad.entity.Client;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service("entityGeneratorImpl")
public class EntityGeneratorImpl implements EntityGenerator {
    private static final Logger logger = Logger.getLogger(EntityGeneratorImpl.class.getName());

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public List<Client> generateClientList() {
        logger.log(Level.FINE, "generateClientList()");

        List<Client> clientList = new ArrayList<>();

        Client client = (Client) applicationContext.getBean("client");
        client.getPerson().setClient(client);

        clientList.add(client);

        return clientList;
    }
}
