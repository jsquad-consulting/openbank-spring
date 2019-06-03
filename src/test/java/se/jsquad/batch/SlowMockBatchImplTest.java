package se.jsquad.batch;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import se.jsquad.batch.status.BatchStatus;
import se.jsquad.batch.status.Status;
import se.jsquad.configuration.ApplicationConfiguration;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ApplicationConfiguration.class, loader = AnnotationConfigContextLoader.class)
public class SlowMockBatchImplTest {
    @Autowired
    private SlowMockBatch slowMockBatch;

    @Test
    public void testStartBatch() throws IllegalAccessException, NoSuchFieldException, InterruptedException {
        // Given
        int seconds = 0;

        Field field = ((SlowMockBatchImpl) slowMockBatch).getClass().getDeclaredField("sleepTime");
        field.setAccessible(true);

        field.set(((SlowMockBatchImpl) slowMockBatch), seconds);

        // When
        BatchStatus batchStatus = slowMockBatch.startBatch();

        // Then
        assertEquals(Status.SUCCESS, batchStatus.getStatus());
        assertEquals("Batch job went just fine.", batchStatus.getMessage());
    }
}
