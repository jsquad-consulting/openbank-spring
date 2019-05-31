package se.jsquad.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import se.jsquad.generator.EntityGenerator;

import javax.inject.Inject;
import javax.inject.Named;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration({"classpath:META-INF/applicationContext.xml"})
public class ClientTest {
    @Inject
    @Named("entityGeneratorImpl")
    private EntityGenerator entityGenerator;

    @Test
    public void testPrototypeScopeForClient() {
        // Given
        Client client1 = entityGenerator.generateClientSet().iterator().next();
        Client client2 = entityGenerator.generateClientSet().iterator().next();

        // Then
        assertNotEquals(client1, client2);
        assertNotEquals(client1.getPerson(), client2.getPerson());
    }
}
