package se.jsquad.rest;

import org.apache.activemq.broker.BrokerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import se.jsquad.batch.SlowMockBatch;
import se.jsquad.batch.SlowMockBatchImpl;
import se.jsquad.batch.status.BatchStatus;
import se.jsquad.batch.status.Status;
import se.jsquad.component.database.FlywayDatabaseMigration;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestPropertySource(locations = {"classpath:test/application.properties",
        "classpath:activemq.properties",
        "classpath:test/configuration/configuration_test.yaml",
        "classpath:test/configuration/openbank_jpa.yaml",
        "classpath:test/configuration/security_jpa.yaml"},
        properties = {"jasypt.encryptor.password = testencryption"})
public class OpenBankRestControllerTest {
    @MockBean
    private BrokerService brokerService;

    @MockBean
    private FlywayDatabaseMigration flywayDatabaseMigration;

    @Autowired
    private OpenBankRestController openBankRestController;

    @Autowired
    private SlowMockBatch slowMockBatch;

    @Test
    public void testGetSlowMockBatchStatus() throws NoSuchFieldException, IllegalAccessException {
        // Given
        int seconds = 0;

        Field field = SlowMockBatchImpl.class.getDeclaredField("sleepTime");
        field.setAccessible(true);

        field.set(slowMockBatch, seconds);

        // When
        ResponseEntity responseEntity = openBankRestController.getOpenBankBatchStatus();
        BatchStatus batchStatus = (BatchStatus) responseEntity.getBody();

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(Status.SUCCESS, batchStatus.getStatus());
        assertEquals("Batch job went just fine.", batchStatus.getMessage());
    }

}
