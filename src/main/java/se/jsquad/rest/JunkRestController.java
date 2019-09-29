package se.jsquad.rest;

import io.swagger.v3.oas.annotations.Operation;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.jsquad.client.info.JunkApi;

@RestController
@RequestMapping(path = "/api")
public class JunkRestController {
    private Logger logger;

    public JunkRestController(Logger logger) {
        logger.log(Level.INFO, "GetClientInformationREST(logger: {})",
                logger);
        this.logger = logger;
    }

    @GetMapping(value = "/junk/info/test/{personIdentification}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(hidden = true)
    public ResponseEntity<JunkApi> getJunkInformation(@PathVariable String junkInformation) {

        return ResponseEntity.ok(new JunkApi());
    }
}