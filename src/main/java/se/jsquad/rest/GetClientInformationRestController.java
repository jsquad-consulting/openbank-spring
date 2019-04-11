package se.jsquad.rest;

import se.jsquad.entity.Client;

public interface GetClientInformationRestController {
    Client getClientInformation(String personIdentification);
}
