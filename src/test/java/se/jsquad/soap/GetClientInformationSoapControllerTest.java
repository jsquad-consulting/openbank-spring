package se.jsquad.soap;

import org.apache.activemq.broker.BrokerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import se.jsquad.getclientservice.GetClientRequest;
import se.jsquad.getclientservice.GetClientResponse;
import se.jsquad.getclientservice.StatusType;
import se.jsquad.getclientservice.TransactionType;
import se.jsquad.getclientservice.Type;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestPropertySource(locations = {"classpath:application.properties", "classpath:activemq.properties",
        "classpath:security_database.properties", "classpath:openbank_database.properties"})
public class GetClientInformationSoapControllerTest {
    @MockBean
    BrokerService brokerService;

    @Autowired
    private GetClientInformationSoapController getClientInformationSoapController;

    @Test
    public void testGetClientInformationWs() {
        // Given
        String personIdentification = "191212121213";

        GetClientRequest getClientRequest = new GetClientRequest();
        getClientRequest.setPersonIdentification(personIdentification);

        // When
        GetClientResponse getClientResponse = getClientInformationSoapController.getClientResponse(getClientRequest);

        // Then
        assertNotNull(getClientResponse);
        assertEquals(StatusType.OK, getClientResponse.getStatus());
        assertEquals("Client found.", getClientResponse.getMessage());

        assertEquals(personIdentification, getClientResponse.getClient().getPerson().getPersonIdentification());
        assertEquals("Alice", getClientResponse.getClient().getPerson().getFirstName());
        assertEquals("Doe", getClientResponse.getClient().getPerson().getLastName());
        assertEquals("alice.doe@test.se", getClientResponse.getClient().getPerson().getMail());

        assertEquals(1, getClientResponse.getClient().getAccountList().size());
        assertEquals(1, getClientResponse.getClient().getAccountList().get(0).getAccountTransactionList().size());

        assertEquals(1000, getClientResponse.getClient().getAccountList().get(0).getBalance());
        assertEquals(TransactionType.WITHDRAWAL,
                getClientResponse.getClient().getAccountList().get(0).getAccountTransactionList().get(0)
                        .getTransactionType());
        assertEquals("500$ in withdrawal",
                getClientResponse.getClient().getAccountList().get(0).getAccountTransactionList().get(0).getMessage());

        assertEquals(Type.PREMIUM, getClientResponse.getClient().getClientType().getType());
        assertEquals(1000, getClientResponse.getClient().getClientType().getPremiumRating());
        assertEquals("Special offer you can not refuse.",
                getClientResponse.getClient().getClientType().getSpecialOffers());
    }
}
