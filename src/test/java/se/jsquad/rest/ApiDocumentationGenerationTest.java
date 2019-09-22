package se.jsquad.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@TestPropertySource(locations = {"classpath:application.properties", "classpath:activemq.properties",
        "classpath:database.properties"})
@SpringBootTest
public class ApiDocumentationGenerationTest {
    @Autowired
    private Environment environment;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void testGetOpenBankAPI() throws Exception {
        // When
        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.get(environment
                .getProperty("springdoc.api-docs.path") + ".yaml")).andReturn();

        // Then
        assertNotNull(mvcResult);

        Files.write(Paths.get("src/main/resources/schema/OpenBankAPIv" + environment.getProperty("api.version") +
                ".yaml"), mvcResult.getResponse().getContentAsByteArray());
    }
}
