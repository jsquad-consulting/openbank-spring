package se.jsquad.business;

import se.jsquad.entity.Client;

public interface OpenBankService {
    Client getClientInformation(String personIdentification);
}
