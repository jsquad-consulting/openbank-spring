package se.jsquad.generator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import se.jsquad.entity.Client;

import java.util.ArrayList;
import java.util.List;

@Service("entityGeneratorImpl")
public class EntityGeneratorImpl implements EntityGenerator {

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public List<Client> generateClientList() {
        List<Client> clientList = new ArrayList<>();

        Client client = (Client) applicationContext.getBean("client");
        client.getPerson().setClient(client);

        clientList.add(client);

        return clientList;
    }
}
