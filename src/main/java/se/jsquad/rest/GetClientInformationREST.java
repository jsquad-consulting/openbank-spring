package se.jsquad.rest;

import se.jsquad.entity.Client;

public interface GetClientInformationREST {
    Client getClientInformation(String personIdentification);
}
