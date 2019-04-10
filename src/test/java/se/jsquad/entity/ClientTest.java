package se.jsquad.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import se.jsquad.generator.EntityGenerator;
import se.jsquad.generator.EntityGeneratorImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration({"classpath:META-INF/applicationContext.xml"})
class ClientTest {
    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void testPrototypeScopeForClient() {
        // Given
        EntityGenerator entityGenerator = (EntityGeneratorImpl) applicationContext.getBean("entityGeneratorImpl");
        Client client1 = entityGenerator.generateClientList().get(0);
        Client client2 = entityGenerator.generateClientList().get(0);

        // Then
        assertNotEquals(client1, client2);
        assertNotEquals(client1.getPerson(), client2.getPerson());

        assertEquals("Mr. Spock", client1.getPerson().getFirstName());
        assertEquals(client1.getPerson().getFirstName(), client2.getPerson().getFirstName());
    }
}
