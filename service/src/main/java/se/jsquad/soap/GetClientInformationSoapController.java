/*
 * Copyright 2021 JSquad AB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package se.jsquad.soap;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import se.jsquad.entity.Account;
import se.jsquad.entity.AccountTransaction;
import se.jsquad.entity.Client;
import se.jsquad.entity.ForeignClient;
import se.jsquad.entity.PremiumClient;
import se.jsquad.entity.RegularClient;
import se.jsquad.getclientservice.AccountTransactionType;
import se.jsquad.getclientservice.AccountType;
import se.jsquad.getclientservice.ClientType;
import se.jsquad.getclientservice.ClientTypeType;
import se.jsquad.getclientservice.GetClientRequest;
import se.jsquad.getclientservice.GetClientResponse;
import se.jsquad.getclientservice.PersonType;
import se.jsquad.getclientservice.StatusType;
import se.jsquad.getclientservice.TransactionType;
import se.jsquad.getclientservice.Type;
import se.jsquad.repository.ClientRepository;

import java.util.Iterator;

@Endpoint
public class GetClientInformationSoapController {
    private ClientRepository clientRepository;
    private Logger logger;

    public GetClientInformationSoapController(Logger logger, ClientRepository clientRepository) {
        this.logger = logger;
        this.clientRepository = clientRepository;
    }


    @PayloadRoot(namespace = "http://jsquad.se/GetClientService/", localPart = "GetClientRequest")
    @ResponsePayload
    public GetClientResponse getClientResponse(@RequestPayload GetClientRequest getClientRequest) {
        GetClientResponse getClientResponse = new GetClientResponse();
        getClientResponse.setClient(null);
        getClientResponse.setMessage("Client not found.");
        getClientResponse.setStatus(StatusType.ERROR);

        if (getClientRequest == null || getClientRequest.getPersonIdentification() == null || getClientRequest.getPersonIdentification().isEmpty()) {
            getClientResponse.setMessage("Request parameter must be set with a proper identification number.");
            return getClientResponse;
        }

        try {
            Client client =
                    clientRepository.getClientByPersonIdentification(getClientRequest.getPersonIdentification());

            createClientType(getClientResponse, client);
            getClientResponse.setStatus(StatusType.OK);
            getClientResponse.setMessage("Client found.");

            return getClientResponse;
        } catch (Exception e) {
            logger.log(Level.ERROR, e.getMessage(), e);

            getClientResponse.setMessage("A system failure has occured.");
            getClientResponse.setClient(null);
            return getClientResponse;
        }
    }

    private void createClientType(GetClientResponse getClientResponse, Client client) {
        se.jsquad.getclientservice.ClientType clientType = new ClientType();
        getClientResponse.setClient(clientType);

        clientType.setPerson(new PersonType());
        clientType.getPerson().setFirstName(client.getPerson().getFirstName());
        clientType.getPerson().setLastName(client.getPerson().getLastName());
        clientType.getPerson().setMail(client.getPerson().getMail());
        clientType.getPerson().setPersonIdentification(client.getPerson().getPersonIdentification());

        clientType.setClientType(new ClientTypeType());

        if (client.getClientType() instanceof RegularClient) {
            clientType.getClientType().setRating(((RegularClient) client.getClientType()).getRating());
            clientType.getClientType().setType(Type.REGULAR);
        } else if (client.getClientType() instanceof PremiumClient) {
            clientType.getClientType().setPremiumRating(((PremiumClient) client.getClientType()).getPremiumRating());
            clientType.getClientType().setSpecialOffers(((PremiumClient) client.getClientType()).getSpecialOffers());
            clientType.getClientType().setType(Type.PREMIUM);
        } else {
            clientType.getClientType().setCountry(((ForeignClient) client.getClientType()).getCountry());
            clientType.getClientType().setType(Type.FOREIGN);
        }

        Iterator<Account> accountIterator = client.getAccountSet().iterator();
        while (accountIterator.hasNext()) {
            Account account = accountIterator.next();

            AccountType accountType = new AccountType();
            accountType.setBalance(account.getBalance());

            if (account.getAccountTransactionSet() != null) {
                Iterator<AccountTransaction> accountTransactionIterator = account.getAccountTransactionSet()
                        .iterator();
                while (accountTransactionIterator.hasNext()) {
                    AccountTransaction accountTransaction = accountTransactionIterator.next();

                    AccountTransactionType accountTransactionType = new AccountTransactionType();
                    accountTransactionType.setMessage(accountTransaction.getMessage());
                    accountTransactionType.setTransactionType(TransactionType.valueOf(accountTransaction
                            .getTransactionType().name()));

                    accountType.getAccountTransactionList().add(accountTransactionType);
                }
            }
            clientType.getAccountList().add(accountType);
        }
    }
}
