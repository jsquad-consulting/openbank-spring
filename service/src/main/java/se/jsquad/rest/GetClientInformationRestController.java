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

package se.jsquad.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import se.jsquad.api.ClientInformation;
import se.jsquad.api.client.ClientApi;
import se.jsquad.api.client.ClientInformationRequest;
import se.jsquad.api.client.ClientInformationResponse;
import se.jsquad.api.client.ClientRequest;
import se.jsquad.api.client.WorldApiResponse;
import se.jsquad.api.time.DateTime;
import se.jsquad.business.OpenBankService;
import se.jsquad.business.WebClientBusiness;
import se.jsquad.exception.ClientNotFoundException;

import java.time.Instant;

@RestController
public class GetClientInformationRestController implements ClientInformation {
    private OpenBankService openBankService;
    private WebClientBusiness webClientBusiness;

    public GetClientInformationRestController(OpenBankService openBankService, WebClientBusiness
            webClientBusiness) {
        this.openBankService = openBankService;
        this.webClientBusiness = webClientBusiness;
    }
    
    @Override
    public ResponseEntity<ClientInformationResponse> updateClientInformation(ClientInformationRequest clientInformationRequest) {
        ClientInformationResponse clientInformationResponse =
                new ClientInformationResponse();

        clientInformationResponse.setClientType(clientInformationRequest.getClientType());
        clientInformationResponse.setPerson(clientInformationRequest.getPerson());

        return ResponseEntity.ok(clientInformationResponse);
    }

    @Override
    public ResponseEntity<WorldApiResponse> getHelloWorld() {
        return ResponseEntity.ok(webClientBusiness.getWorldApiResponse());
    }
    
    @Override
    public ResponseEntity<ClientApi> getClientInformation(String personIdentification) {
        ClientApi clientApi = openBankService.getClientInformationByPersonIdentification(personIdentification);

        if (clientApi == null) {
            throw new ClientNotFoundException("Client not found.");
        }

        return ResponseEntity.ok(clientApi);
    }
    
    @Override
    public ResponseEntity<ClientApi> getClientInformationByRequestBody(ClientRequest clientRequest) {
        ClientApi clientApi = openBankService.getClientInformationByPersonIdentification(clientRequest
                .getClientData().getPersonIdentificationNumber());

        if (clientApi == null) {
            throw new ClientNotFoundException("Client not found.");
        }

        return ResponseEntity.ok(clientApi);
    }

    @Override
    public ResponseEntity<DateTime> getDateTime(String dateTime) {
        DateTime dateTimeResponse = new DateTime();
        dateTimeResponse.setDateTime(Instant.parse(dateTime).toString());
        return ResponseEntity.ok(dateTimeResponse);
    }

}
