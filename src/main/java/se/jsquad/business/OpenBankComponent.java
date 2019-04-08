package se.jsquad.business;

import se.jsquad.entity.Client;

public interface OpenBankComponent {
    Client getClientInformation(String personIdentification);
}
