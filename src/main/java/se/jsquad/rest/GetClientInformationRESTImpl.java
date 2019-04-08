package se.jsquad.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import se.jsquad.business.OpenBankComponent;
import se.jsquad.entity.Client;

import java.util.logging.Level;
import java.util.logging.Logger;

@Controller("getClientInformationRESTImpl")
public class GetClientInformationRESTImpl implements GetClientInformationREST {
    private static Logger logger = Logger.getLogger(GetClientInformationRESTImpl.class.getName());

    private OpenBankComponent openBankComponent;

    @Autowired
    private GetClientInformationRESTImpl(OpenBankComponent openBankComponent) {
        this.openBankComponent = openBankComponent;
    }

    @Override
    public Client getClientInformation(String personIdentification) {
        logger.log(Level.FINE, "getClientInformation(personIdentification: {0})", new Object[]{"hidden"});
        return openBankComponent.getClientInformation(personIdentification);
    }
}
