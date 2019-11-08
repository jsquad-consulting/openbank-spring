package se.jsquad.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.AnnotationConfigWebContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;
import se.jsquad.configuration.ApplicationConfiguration;
import se.jsquad.generator.EntityGenerator;

import javax.inject.Inject;
import javax.inject.Named;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = ApplicationConfiguration.class, loader = AnnotationConfigWebContextLoader.class)
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
