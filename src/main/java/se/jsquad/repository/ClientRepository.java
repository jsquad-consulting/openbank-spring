package se.jsquad.repository;

import se.jsquad.entity.Client;

public interface ClientRepository {
    Client getClientInformation(String personIdentification);

    void persistClient(Client client);
}
