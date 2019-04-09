package se.jsquad.rest;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import se.jsquad.business.OpenBankComponent;
import se.jsquad.entity.Client;

@Controller("getClientInformationRESTImpl")
public class GetClientInformationRESTImpl implements GetClientInformationREST {
    private static Logger logger = LogManager.getLogger(GetClientInformationRESTImpl.class.getName());

    private OpenBankComponent openBankComponent;

    @Autowired
    private GetClientInformationRESTImpl(OpenBankComponent openBankComponent) {
        logger.log(Level.INFO, "GetClientInformationREST(openBankComponent: {}",
                openBankComponent);
        this.openBankComponent = openBankComponent;
    }

    @Override
    public Client getClientInformation(String personIdentification) {
        logger.log(Level.INFO, "getClientInformation(personIdentification: {})", "hidden");
        return openBankComponent.getClientInformation(personIdentification);
    }
}
