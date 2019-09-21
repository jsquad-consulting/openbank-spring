package se.jsquad.rest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import se.jsquad.client.info.AccountApi;
import se.jsquad.client.info.AccountTransactionApi;
import se.jsquad.client.info.ClientApi;
import se.jsquad.client.info.TransactionTypeApi;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@TestPropertySource(locations = {"classpath:application.properties", "classpath:activemq.properties",
        "classpath:database.properties"})
@SpringBootTest
@Transactional(propagation = Propagation.REQUIRED)
public class GetClientInformationRestControllerImplTest {
    @Autowired
    @Qualifier("getClientInformationRestController")
    private GetClientInformationRestController getClientInformationRESTController;

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void testGetClientInformation() {
        // Given
        String personIdentification = "191212121212";

        // When
        ResponseEntity responseEntity = getClientInformationRESTController.getClientInformation(personIdentification);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        ClientApi clientApi = (ClientApi) responseEntity.getBody();

        assertEquals(personIdentification, clientApi.getPerson().getPersonIdentification());
        assertEquals("John", clientApi.getPerson().getFirstName());
        assertEquals("Doe", clientApi.getPerson().getLastName());
        assertEquals("john.doe@test.se", clientApi.getPerson().getMail());

        assertEquals(500, clientApi.getClientType().getRating());

        AccountApi accountApi = clientApi.getAccountList().get(0);

        assertEquals(500, accountApi.getBalance());

        AccountTransactionApi accountTransactionApi = accountApi.getAccountTransactionList().get(0);

        assertEquals("500$ in deposit", accountTransactionApi.getMessage());
        assertEquals(TransactionTypeApi.DEPOSIT, accountTransactionApi.getTransactionType());
    }

    @Test
    public void testSingleton() {
        // Given
        GetClientInformationRestController getClientInformationRestController1 = (GetClientInformationRestController)
                applicationContext.getBean(
                        "getClientInformationRestController");

        GetClientInformationRestController getClientInformationRestController2 = (GetClientInformationRestController)
                applicationContext.getBean(
                        "getClientInformationRestController");

        // Then
        assertEquals(getClientInformationRestController1, getClientInformationRestController2);
    }
}
