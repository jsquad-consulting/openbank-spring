package se.jsquad.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import se.jsquad.generator.EntityGenerator;

import javax.inject.Inject;
import javax.inject.Named;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration({"classpath:META-INF/applicationContext.xml"})
class ClientTest {
    @Inject
    @Named("entityGeneratorImpl")
    private EntityGenerator entityGenerator;

    @Test
    public void testPrototypeScopeForClient() {
        // Given
        Client client1 = entityGenerator.generateClientList().get(0);
        Client client2 = entityGenerator.generateClientList().get(0);

        // Then
        assertNotEquals(client1, client2);
        assertNotEquals(client1.getPerson(), client2.getPerson());

        assertEquals("Mr. Spock", client1.getPerson().getFirstName());
        assertEquals(client1.getPerson().getFirstName(), client2.getPerson().getFirstName());
    }
}
