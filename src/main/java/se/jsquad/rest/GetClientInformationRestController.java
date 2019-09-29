package se.jsquad.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.jsquad.business.OpenBankService;
import se.jsquad.client.info.ClientApi;
import se.jsquad.exception.ClientNotFoundException;
import se.jsquad.exception.IllegalPersonIdentificationNumberException;

@RestController
@RequestMapping(path = "/api")
public class GetClientInformationRestController {
    private Logger logger;
    private OpenBankService openBankService;

    @Autowired
    private GetClientInformationRestController(Logger logger, OpenBankService openBankService) {
        logger.log(Level.INFO, "GetClientInformationREST(logger: {}, openBankService: {})",
                logger, openBankService);
        this.logger = logger;
        this.openBankService = openBankService;
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
                    @ApiResponse(responseCode = "500", description = "Severe system failure has occured!", content =
                    @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(
                            example = "Severe system failure has occured!")))})
    public ResponseEntity getClientInformation(@Parameter(description = "The person identification number",
            example = "191212121212", required = true) @PathVariable String personIdentification) {
        logger.log(Level.INFO, "getClientByPersonIdentification(personIdentification: {})", "hidden");

        if (personIdentification == null || personIdentification.isEmpty()
                || !personIdentification.matches("[0-9]{12}")) {
            throw new IllegalPersonIdentificationNumberException("Personal identification number can't be empty and " +
                    "it must be twelve digits.");
        }

            ClientApi clientApi = openBankService.getClientInformationByPersonIdentification(personIdentification);

            if (clientApi == null) {
                throw new ClientNotFoundException("Client not found.");
            }

            return ResponseEntity.ok(clientApi);
    }
}
