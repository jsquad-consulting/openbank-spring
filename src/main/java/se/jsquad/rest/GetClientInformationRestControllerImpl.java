package se.jsquad.rest;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import se.jsquad.business.OpenBankService;
import se.jsquad.entity.Client;

@Controller("getClientInformationRestControllerImpl")
public class GetClientInformationRestControllerImpl implements GetClientInformationRestController {
    private static Logger logger = LogManager.getLogger(GetClientInformationRestControllerImpl.class.getName());

    private OpenBankService openBankService;

    @Autowired
    private GetClientInformationRestControllerImpl(OpenBankService openBankService) {
        logger.log(Level.INFO, "GetClientInformationREST(openBankComponent: {}",
                openBankService);
        this.openBankService = openBankService;
    }

    @Override
    public Client getClientInformation(String personIdentification) {
        logger.log(Level.INFO, "getClientInformation(personIdentification: {})", "hidden");
        return openBankService.getClientInformation(personIdentification);
    }
}
