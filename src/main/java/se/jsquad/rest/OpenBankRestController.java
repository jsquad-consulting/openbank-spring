package se.jsquad.rest;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import se.jsquad.batch.status.BatchStatus;
import se.jsquad.business.OpenBankService;

import java.util.concurrent.Future;

@Controller("openBankRestController")
@RequestMapping(path = "/api")
public class OpenBankRestController {
    private Logger logger;
    private OpenBankService openBankService;

    public OpenBankRestController(Logger logger,
                                  OpenBankService openBankService) {
        logger.log(Level.INFO, "OpenBankRestController(logger: {}, openBankService: {})",
                logger, openBankService);
        this.logger = logger;
        this.openBankService = openBankService;
    }

    @GetMapping(value = "/openbank/start/slow/batch/mock", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity getOpenBankBatchStatus() {
        logger.log(Level.INFO, "getOpenBankBatchStatus()");

        try {
            Future<BatchStatus> batchStatusFuture = openBankService.startSlowBatch();

            return ResponseEntity.ok().body(batchStatusFuture.get());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.TEXT_PLAIN)
                    .body("System failure has occured");
        }
    }
}
