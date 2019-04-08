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
public class GetClientInformationRESTImplTest {
    @Autowired
    @Qualifier("getClientInformationRESTImpl")
    private GetClientInformationREST getClientInformationREST;

    @Autowired
    ApplicationContext applicationContext;

    @Test
    public void testGetClientInformation() {
        // When
        Client client = getClientInformationREST.getClientInformation("191212121212");

        // Then
        assertEquals("Mr. Spock", client.getPerson().getFirstName());
        assertEquals("191212121212", client.getPerson().getPersonIdentification());
    }

    @Test
    public void testSingleton() {
        // Given
        GetClientInformationREST getClientInformationREST1 = (GetClientInformationREST) applicationContext.getBean(
                "getClientInformationRESTImpl");

        GetClientInformationREST getClientInformationREST2 = (GetClientInformationREST) applicationContext.getBean(
                "getClientInformationRESTImpl");

        // Then
        assertEquals(getClientInformationREST1, getClientInformationREST2);
    }
}
