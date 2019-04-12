package se.jsquad.repository;

import se.jsquad.entity.Client;

public interface ClientRepository {
    Client getClientByPersonIdentification(String personIdentification);
    void persistClient(Client client);
}
