package se.jsquad.rest;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.jsquad.business.OpenBankService;
import se.jsquad.client.info.ClientApi;

@RestController("getClientInformationRestController")
@RequestMapping(path = "/api")
public class GetClientInformationRestController {
    private Logger logger;
    private OpenBankService openBankService;

    @Autowired
    private GetClientInformationRestController(@Qualifier("logger") Logger logger,
                                               OpenBankService openBankService) {
        logger.log(Level.INFO, "GetClientInformationREST(logger: {}, openBankService: {})",
                logger, openBankService);
        this.logger = logger;
        this.openBankService = openBankService;
    }

    @GetMapping(value = "/client/info/{personIdentification}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity getClientInformation(@PathVariable String personIdentification) {
        logger.log(Level.INFO, "getClientByPersonIdentification(personIdentification: {})", "hidden");

        if (personIdentification == null || personIdentification.isEmpty()) {
            String message = "Person identification can not be empty.";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.TEXT_PLAIN).body(message);
        }

        try {
            ClientApi clientApi = openBankService.getClientInformationByPersonIdentification(personIdentification);

            if (clientApi == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.TEXT_PLAIN)
                        .body("Client not found.");
            }

            return ResponseEntity.ok().body(clientApi);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.TEXT_PLAIN)
                    .body("System failure has occured");
        }
    }
}
