package se.jsquad.business;

import se.jsquad.client.info.ClientApi;

public interface OpenBankService {
    ClientApi getClientInformationByPersonIdentification(String personIdentification);
}
