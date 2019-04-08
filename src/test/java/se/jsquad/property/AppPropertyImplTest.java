package se.jsquad.property;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration({"classpath:META-INF/applicationContext.xml"})
public class AppPropertyImplTest {
    @Autowired
    @Qualifier("appProperty")
    private AppProperty appProperty;

    @Test
    public void testAppProperty() {
        // Then
        assertEquals("OpenBank", appProperty.getName());
        assertEquals("1.0.0", appProperty.getVersion());
    }
}
