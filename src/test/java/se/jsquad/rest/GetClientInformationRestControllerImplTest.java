package se.jsquad.rest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import se.jsquad.entity.Client;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration({"classpath:META-INF/applicationContext.xml"})
public class GetClientInformationRestControllerImplTest {
    @Autowired
    @Qualifier("getClientInformationRestController")
    private GetClientInformationRestController getClientInformationRESTController;

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void testGetClientInformation() {
        // When
        Client client = getClientInformationRESTController.getClientInformation("191212121212");

        // Then
        assertEquals("John", client.getPerson().getFirstName());
        assertEquals("191212121212", client.getPerson().getPersonIdentification());
    }

    @Test
    public void testSingleton() {
        // Given
        GetClientInformationRestController getClientInformationRestController1 = (GetClientInformationRestController)
                applicationContext.getBean(
                        "getClientInformationRestControllerImpl");

        GetClientInformationRestController getClientInformationRestController2 = (GetClientInformationRestController)
                applicationContext.getBean(
                        "getClientInformationRestControllerImpl");

        // Then
        assertEquals(getClientInformationRestController1, getClientInformationRestController2);
    }
}
