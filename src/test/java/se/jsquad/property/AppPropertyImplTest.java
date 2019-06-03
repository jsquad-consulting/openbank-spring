package se.jsquad.property;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import se.jsquad.configuration.ApplicationConfiguration;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ApplicationConfiguration.class, loader = AnnotationConfigContextLoader.class)
public class AppPropertyImplTest {
    @Autowired
    @Qualifier("appPropertyConfiguration")
    private AppPropertyConfiguration appPropertyConfiguration;

    @Test
    public void testAppProperty() {
        // Then
        assertEquals("OpenBank", appPropertyConfiguration.getName());
        assertEquals("1.0.0", appPropertyConfiguration.getVersion());
    }
}
