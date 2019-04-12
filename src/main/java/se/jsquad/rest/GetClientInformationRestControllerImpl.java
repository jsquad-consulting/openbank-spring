package se.jsquad.rest;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import se.jsquad.business.OpenBankService;
import se.jsquad.entity.Client;

@Controller("getClientInformationRestControllerImpl")
@Qualifier("getClientInformationRestController")
public class GetClientInformationRestControllerImpl implements GetClientInformationRestController {
    private Logger logger;

    private OpenBankService openBankService;

    @Autowired
    private GetClientInformationRestControllerImpl(@Qualifier("logger") Logger logger,
                                                   OpenBankService openBankService) {
        logger.log(Level.INFO, "GetClientInformationREST(logger: {}, openBankComponent: {})",
                logger, openBankService);
        this.logger = logger;
        this.openBankService = openBankService;
    }

    @Override
    public Client getClientInformation(String personIdentification) {
        logger.log(Level.INFO, "getClientByPersonIdentification(personIdentification: {})", "hidden");

        return openBankService.getClientInformationByPersonIdentification(personIdentification);
    }
}
