package se.jsquad.generator;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import se.jsquad.entity.Account;
import se.jsquad.entity.AccountTransaction;
import se.jsquad.entity.Client;
import se.jsquad.entity.Person;
import se.jsquad.entity.PremiumClient;
import se.jsquad.entity.RegularClient;
import se.jsquad.entity.TransactionType;

import javax.inject.Named;
import java.util.HashSet;
import java.util.Set;


@Named(value = "entityGeneratorImpl")
public class EntityGeneratorImpl implements EntityGenerator {
    private Logger logger;

    public static final String SPECIAL_OFFER_YOU_CAN_NOT_REFUSE = "Special offer you can not refuse.";
    public static final String IN_WITHDRAWAL = "500$ in withdrawal";

    public EntityGeneratorImpl(@Qualifier("logger") Logger logger) {
        logger.log(Level.INFO, "EntityGeneratorImpl(logger: {})", logger);
        this.logger = logger;
    }

    @Override
    public Set<Client> generateClientSet() {
        logger.log(Level.INFO, "generateClientSet()");
        Set<Client> clientSet = new HashSet<>();

        Client client1 = new Client();
        client1.setClientType(new RegularClient());
        client1.getClientType().setClient(client1);
        ((RegularClient) client1.getClientType()).setRating(Long.valueOf(500));

        client1.setAccountSet(new HashSet<>());

        Account account = new Account();
        account.setBalance(Long.valueOf(500));
        account.setAccountNumber("1000");
        account.setClient(client1);

        AccountTransaction accountTransaction = new AccountTransaction();
        accountTransaction.setMessage("500$ in deposit");
        accountTransaction.setTransactionType(TransactionType.DEPOSIT);
        accountTransaction.setAccount(account);

        account.setAccountTransactionSet(new HashSet<>());
        account.getAccountTransactionSet().add(accountTransaction);

        client1.getAccountSet().add(account);
        client1.setPerson(new Person());

        client1.getPerson().setClient(client1);
        client1.getPerson().setFirstName("John");
        client1.getPerson().setLastName("Doe");
        client1.getPerson().setPersonIdentification("191212121212");
        client1.getPerson().setMail("john.doe@test.se");

        clientSet.add(client1);

        client1 = new Client();
        client1.setClientType(new PremiumClient());
        client1.getClientType().setClient(client1);
        ((PremiumClient) client1.getClientType()).setPremiumRating(Long.valueOf(1000));
        ((PremiumClient) client1.getClientType()).setSpecialOffers(SPECIAL_OFFER_YOU_CAN_NOT_REFUSE);

        client1.setAccountSet(new HashSet<>());

        account = new Account();
        account.setBalance(Long.valueOf(1000));
        account.setAccountNumber("1001");
        account.setClient(client1);

        accountTransaction = new AccountTransaction();
        accountTransaction.setMessage(IN_WITHDRAWAL);
        accountTransaction.setTransactionType(TransactionType.WITHDRAWAL);
        accountTransaction.setAccount(account);

        account.setAccountTransactionSet(new HashSet<>());
        account.getAccountTransactionSet().add(accountTransaction);

        client1.getAccountSet().add(account);

        client1.setPerson(new Person());

        client1.getPerson().setClient(client1);
        client1.getPerson().setFirstName("Alice");
        client1.getPerson().setLastName("Doe");
        client1.getPerson().setPersonIdentification("191212121213");
        client1.getPerson().setMail("alice.doe@test.se");

        clientSet.add(client1);

        client1 = new Client();
        client1.setClientType(new PremiumClient());
        client1.getClientType().setClient(client1);
        ((PremiumClient) client1.getClientType()).setPremiumRating(Long.valueOf(1000));
        ((PremiumClient) client1.getClientType()).setSpecialOffers(SPECIAL_OFFER_YOU_CAN_NOT_REFUSE);

        client1.setAccountSet(new HashSet<>());

        account = new Account();
        account.setBalance(Long.valueOf(1000));
        account.setAccountNumber("1051");
        account.setClient(client1);

        accountTransaction = new AccountTransaction();
        accountTransaction.setMessage(IN_WITHDRAWAL);
        accountTransaction.setTransactionType(TransactionType.WITHDRAWAL);
        accountTransaction.setAccount(account);

        account.setAccountTransactionSet(new HashSet<>());
        account.getAccountTransactionSet().add(accountTransaction);

        client1.getAccountSet().add(account);

        client1.setPerson(new Person());

        client1.getPerson().setClient(client1);
        client1.getPerson().setFirstName("Alice");
        client1.getPerson().setLastName("Doe");
        client1.getPerson().setPersonIdentification("191212121221");
        client1.getPerson().setMail("alice.doe@test.se");

        clientSet.add(client1);

        client1 = new Client();
        client1.setClientType(new PremiumClient());
        client1.getClientType().setClient(client1);
        ((PremiumClient) client1.getClientType()).setPremiumRating(Long.valueOf(1000));
        ((PremiumClient) client1.getClientType()).setSpecialOffers(SPECIAL_OFFER_YOU_CAN_NOT_REFUSE);

        client1.setAccountSet(new HashSet<>());

        account = new Account();
        account.setBalance(Long.valueOf(500));
        account.setAccountNumber("1050");
        account.setClient(client1);

        accountTransaction = new AccountTransaction();
        accountTransaction.setMessage(IN_WITHDRAWAL);
        accountTransaction.setTransactionType(TransactionType.WITHDRAWAL);
        accountTransaction.setAccount(account);

        account.setAccountTransactionSet(new HashSet<>());
        account.getAccountTransactionSet().add(accountTransaction);

        client1.getAccountSet().add(account);

        client1.setPerson(new Person());

        client1.getPerson().setClient(client1);
        client1.getPerson().setFirstName("John");
        client1.getPerson().setLastName("Doe");
        client1.getPerson().setPersonIdentification("191212121220");
        client1.getPerson().setMail("john.doe@test.se");

        clientSet.add(client1);

        return clientSet;
    }
}
