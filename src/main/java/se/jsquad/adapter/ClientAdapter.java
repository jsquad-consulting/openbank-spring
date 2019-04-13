package se.jsquad.adapter;

import se.jsquad.client.info.ClientApi;
import se.jsquad.entity.Client;

public interface ClientAdapter {
    ClientApi translateClientToClientApi(Client client);

    Client translateClientApiToClient(ClientApi clientApi);
}
