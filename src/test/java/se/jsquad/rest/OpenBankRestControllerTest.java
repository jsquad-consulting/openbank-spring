package se.jsquad.rest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import se.jsquad.batch.SlowMockBatch;
import se.jsquad.batch.SlowMockBatchImpl;
import se.jsquad.batch.status.BatchStatus;
import se.jsquad.batch.status.Status;
import se.jsquad.configuration.ApplicationConfiguration;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ApplicationConfiguration.class, loader = AnnotationConfigContextLoader.class)
class OpenBankRestControllerTest {
    @Autowired
    private OpenBankRestController openBankRestController;

    @Autowired
    private SlowMockBatch slowMockBatch;

    @Test
    public void testGetSlowMockBatchStatus() throws NoSuchFieldException, IllegalAccessException {
        // Given
        int seconds = 0;

        Field field = ((SlowMockBatchImpl) slowMockBatch).getClass().getDeclaredField("sleepTime");
        field.setAccessible(true);

        field.set(((SlowMockBatchImpl) slowMockBatch), seconds);

        // When
        ResponseEntity responseEntity = openBankRestController.getOpenBankBatchStatus();
        BatchStatus batchStatus = (BatchStatus) responseEntity.getBody();

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(Status.SUCCESS, batchStatus.getStatus());
        assertEquals("Batch job went just fine.", batchStatus.getMessage());
    }

}
