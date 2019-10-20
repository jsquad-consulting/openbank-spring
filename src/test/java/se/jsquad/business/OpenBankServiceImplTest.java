package se.jsquad.business;

import org.apache.activemq.broker.BrokerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import se.jsquad.client.info.AccountApi;
import se.jsquad.client.info.AccountTransactionApi;
import se.jsquad.client.info.ClientApi;
import se.jsquad.client.info.TransactionTypeApi;
import se.jsquad.component.database.FlywayDatabaseMigration;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestPropertySource(locations = {"classpath:application.properties", "classpath:activemq.properties",
        "classpath:test/configuration/configuration_test.yaml",
        "classpath:test/configuration/openbank_jpa.yaml",
        "classpath:test/configuration/security_jpa.yaml"},
        properties = {"jasypt.encryptor.password = testencryption"})
public class OpenBankServiceImplTest {
    @MockBean
    private BrokerService brokerService;

    @MockBean
    private FlywayDatabaseMigration flywayDatabaseMigration;

    @Autowired
    private OpenBankService openBankService;

    @Test
    void testGetClientInformation() {
        // Given
        String personIdentification = "191212121212";

        // When
        ClientApi clientApi = openBankService.getClientInformationByPersonIdentification(personIdentification);

        // Then
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
}
