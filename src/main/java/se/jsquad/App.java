package se.jsquad;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import se.jsquad.property.AppProperty;
import se.jsquad.rest.GetClientInformationREST;

import java.util.logging.Level;
import java.util.logging.Logger;

public class App {
    private static Logger logger = Logger.getLogger(App.class.getName());

    public static void main(String[] arguments) {
        logger.log(Level.FINE, "main(arguments: {0})", new Object[]{arguments});

        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(App.class.getResource(
                "/META-INF/applicationContext.xml").toString());

        GetClientInformationREST getClientInformationREST = applicationContext.getBean("getClientInformationRESTImpl",
                GetClientInformationREST.class);

        AppProperty appProperty = applicationContext.getBean("appProperty", AppProperty.class);
        appProperty.getVersion();
        appProperty.getName();

        getClientInformationREST.getClientInformation("191212");

        applicationContext.close();
    }
}
