package se.jsquad.business;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import se.jsquad.entity.Account;
import se.jsquad.entity.AccountTransaction;
import se.jsquad.entity.Client;
import se.jsquad.entity.RegularClient;
import se.jsquad.entity.TransactionType;

import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration({"classpath:META-INF/applicationContext.xml"})
@Transactional
class OpenBankServiceImplTest {
    @Autowired
    @Qualifier("openBankService")
    private OpenBankServiceImpl openBankService;

    @Test
    void testGetClientInformation() {
        // Given
        String personIdentification = "191212121212";

        // When
        Client client = openBankService.getClientInformationByPersonIdentification(personIdentification);

        // Then
        assertEquals(personIdentification, client.getPerson().getPersonIdentification());

        assertEquals("John", client.getPerson().getFirstName());
        assertEquals("Doe", client.getPerson().getLastName());
        assertEquals("john.doe@test.se", client.getPerson().getMail());
        assertEquals(client, client.getPerson().getClient());

        assertEquals(500, ((RegularClient) client.getClientType()).getRating());
        assertEquals(client, client.getClientType().getClient());

        Account account = client.getAccountSet().iterator().next();

        assertEquals("1000", account.getAccountNumber());
        assertEquals(500, account.getBalance());
        assertEquals(client, account.getClient());

        AccountTransaction accountTransaction = account.getAccountTransactionSet().iterator().next();

        assertEquals(account, accountTransaction.getAccount());
        assertEquals("500$ in deposit", accountTransaction.getMessage());
        assertEquals(TransactionType.DEPOSIT, accountTransaction.getTransactionType());
    }
}
