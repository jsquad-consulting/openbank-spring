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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.jsquad.business.OpenBankService;
import se.jsquad.business.WebClientBusiness;
import se.jsquad.client.info.ClientApi;
import se.jsquad.client.info.ClientInformationRequest;
import se.jsquad.client.info.ClientInformationResponse;
import se.jsquad.client.info.ClientRequest;
import se.jsquad.client.info.WorldApiResponse;
import se.jsquad.date.time.DateTime;
import se.jsquad.exception.ClientNotFoundException;
import se.jsquad.validator.ClientInformationBodyConstraint;
import se.jsquad.validator.ClientRequestBodyConstraint;
import se.jsquad.validator.PersonIdentificationNumberConstraint;

import java.time.Instant;

@RestController
@RequestMapping(path = "/api")
@Validated
public class GetClientInformationRestController {
    private Logger logger;
    private OpenBankService openBankService;
    private WebClientBusiness webClientBusiness;

    public GetClientInformationRestController(Logger logger, OpenBankService openBankService, WebClientBusiness
            webClientBusiness) {
        this.logger = logger;
        this.openBankService = openBankService;
        this.webClientBusiness = webClientBusiness;
    }

    @PutMapping(value = "/update/client/information", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces =
            {MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "Update client information, mock implementation",
            description = "Update the client information based with common constraint vaidation for ClientApi xsd " +
                    "model.",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Get ClientApi model as base response", content = @Content(mediaType =
                            MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ClientInformationResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Bad content", content =
                    @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(
                            example = "ClientApi payload contains bad content."))),
                    @ApiResponse(responseCode = "500", description = "Severe system failure has occured!", content =
                    @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(
                            example = "Severe system failure has occured!")))})
    public ResponseEntity<ClientInformationResponse> updateClientInformation(@RequestBody @ClientInformationBodyConstraint
                                                                                        ClientInformationRequest clientInformationRequest) {
        ClientInformationResponse clientInformationResponse =
                new ClientInformationResponse();

        clientInformationResponse.setClientType(clientInformationRequest.getClientType());
        clientInformationResponse.setPerson(clientInformationRequest.getPerson());

        return ResponseEntity.ok(clientInformationResponse);
    }

    @GetMapping(value = "/get/hello/world", produces = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(hidden = false, summary = "Get hello world by the remote http server",
            description = "Get The hello world by the remote HTTP REST server",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Hello world", content = @Content(mediaType =
                            MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = WorldApiResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Severe system failure has occured!", content =
                    @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(
                            example = "Severe system failure has occured!")))})
    public ResponseEntity<WorldApiResponse> getHelloWorld() {
        return ResponseEntity.ok(webClientBusiness.getWorldApiResponse());
    }

    @GetMapping(value = "/client/info/{personIdentification}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "Get client by person identification number",
            description = "Get the ClientAPI response object with uniqueue personal identification number as " +
                    "parameter argument.",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "The client", content = @Content(mediaType =
                            MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ClientApi.class))),
                    @ApiResponse(responseCode = "400",
                            description = "Invalid personal identification number", content = @Content(mediaType =
                            MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(example = "Invalid personal identification number."))),
                    @ApiResponse(responseCode = "404",
                            description = "Client not found.", content = @Content(mediaType =
                            MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(example = "Client not found."))),
                    @ApiResponse(responseCode = "500", description = "Severe system failure has occured!", content =
                    @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(
                            example = "Severe system failure has occured!")))})
    public ResponseEntity<ClientApi> getClientInformation(@Parameter(description = "The person identification number",
            example = "191212121212", required = true) @PathVariable @PersonIdentificationNumberConstraint
                                                                  String personIdentification) {
        ClientApi clientApi = openBankService.getClientInformationByPersonIdentification(personIdentification);

        if (clientApi == null) {
            throw new ClientNotFoundException("Client not found.");
        }

        return ResponseEntity.ok(clientApi);
    }

    @GetMapping(value = "/get/client/info/", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces =
            {MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "Get client by client request body",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "ClientRequest body.",
                    content = @Content(schema = @Schema(implementation = ClientRequest.class)), required = true),
            description = "Get the ClientAPI response object with uniqueue personal identification number as " +
                    "part of the request body.",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "The client", content = @Content(mediaType =
                            MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ClientApi.class))),
                    @ApiResponse(responseCode = "400",
                            description = "Invalid personal identification number", content = @Content(mediaType =
                            MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(example = "Invalid personal identification number."))),
                    @ApiResponse(responseCode = "404",
                            description = "Client not found.", content = @Content(mediaType =
                            MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(example = "Client not found."))),
                    @ApiResponse(responseCode = "500", description = "Severe system failure has occured!", content =
                    @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(
                            example = "Severe system failure has occured!")))})
    public ResponseEntity<ClientApi> getClientInformationByRequestBody(@Parameter(hidden = true)
                                                                       @ClientRequestBodyConstraint
                                                                       @RequestBody ClientRequest clientRequest) {
        ClientApi clientApi = openBankService.getClientInformationByPersonIdentification(clientRequest
                .getClientData().getPersonIdentificationNumber());

        if (clientApi == null) {
            throw new ClientNotFoundException("Client not found.");
        }

        return ResponseEntity.ok(clientApi);
    }

    @GetMapping(value = "/date/time/{dateTime}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "Get date time",
            description = "Get the date time RFC3339 string",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "The date time", content = @Content(mediaType =
                            MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = DateTime.class))),
                    @ApiResponse(responseCode = "400",
                            description = "Date time", content = @Content(mediaType =
                            MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(example = "Invalid date time format."))),
                    @ApiResponse(responseCode = "500", description = "Severe system failure has occured!", content =
                    @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(
                            example = "Severe system failure has occured!")))})
    public ResponseEntity<DateTime> getDateTime(@Parameter(description = "The get date time.",
            example = "2019-11-01T00:00:00ZZ", required = true) @PathVariable("dateTime") String dateTime) {
        DateTime dateTimeResponse = new DateTime();
        dateTimeResponse.setDateTime(Instant.parse(dateTime).toString());
        return ResponseEntity.ok(dateTimeResponse);
    }

}
