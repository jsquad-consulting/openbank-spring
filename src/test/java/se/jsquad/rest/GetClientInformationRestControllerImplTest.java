package se.jsquad.rest;

import org.apache.activemq.broker.BrokerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import se.jsquad.client.info.AccountApi;
import se.jsquad.client.info.AccountTransactionApi;
import se.jsquad.client.info.ClientApi;
import se.jsquad.client.info.TransactionTypeApi;

import javax.validation.ConstraintViolationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@TestPropertySource(locations = {"classpath:application.properties", "classpath:activemq.properties",
        "classpath:openbank_database.properties", "classpath:security_database.properties"})
@SpringBootTest
@Transactional(transactionManager = "transactionManagerOpenBank", propagation = Propagation.REQUIRED)
public class GetClientInformationRestControllerImplTest {
    @MockBean
    BrokerService brokerService;

    @Autowired
    private GetClientInformationRestController getClientInformationRESTController;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private WebApplicationContext context;

    @Test
    public void testGetClientInformation() {
        // Given
        String personIdentification = "191212121212";

        // When
        ResponseEntity<ClientApi> responseEntity =
                getClientInformationRESTController.getClientInformation(personIdentification);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        ClientApi clientApi = responseEntity.getBody();

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
    public void testResponseEntityPersonIdentificationNumberInvalid() throws Exception {
        // Given
        String personIdentificationNumber = "123";
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

        // When and then
        mockMvc.perform(get("/api/client/info/" + personIdentificationNumber)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Person identification number must be twelve digits."));

    }

    @Test
    public void testInvalidPersonIdentificationNumberNull() {
        // Given
        String personalIdentificationNumber = null;

        // When
        Throwable throwable = assertThrows(ConstraintViolationException.class, () ->
                getClientInformationRESTController.getClientInformation(personalIdentificationNumber));

        // Then
        assertEquals("Person identification number must be twelve digits.",
                ((ConstraintViolationException) throwable).getConstraintViolations().iterator().next().getMessage());
    }

    @Test
    public void testInvalidPersonIdentificationNumberEmpty() {
        // Given
        String personalIdentificationNumber = "";

        // When
        Throwable throwable = assertThrows(ConstraintViolationException.class, () ->
                getClientInformationRESTController.getClientInformation(personalIdentificationNumber));

        // Then
        assertEquals("Person identification number must be twelve digits.",
                ((ConstraintViolationException) throwable).getConstraintViolations().iterator().next().getMessage());
    }

    @Test
    public void testInvalidPersonIdentificationNumberLessThenTwelveDigits() {
        // Given
        String personalIdentificationNumber = "123";

        // When
        Throwable throwable = assertThrows(ConstraintViolationException.class, () ->
                getClientInformationRESTController.getClientInformation(personalIdentificationNumber));

        // Then
        assertEquals("Person identification number must be twelve digits.",
                ((ConstraintViolationException) throwable).getConstraintViolations().iterator().next().getMessage());
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
