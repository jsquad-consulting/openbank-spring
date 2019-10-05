package se.jsquad.batch;

import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import se.jsquad.batch.status.BatchStatus;
import se.jsquad.batch.status.Status;
import se.jsquad.property.AppPropertyConfiguration;

import java.util.concurrent.TimeUnit;

@Service
public class SlowMockBatchImpl implements SlowMockBatch {
    private int sleepTime;

    private Logger logger;
    private AppPropertyConfiguration appPropertyConfiguration;

    public SlowMockBatchImpl(Logger logger, AppPropertyConfiguration appPropertyConfiguration) {
        this.logger = logger;
        this.appPropertyConfiguration = appPropertyConfiguration;
        sleepTime = this.appPropertyConfiguration.getBatchSleepTime();
    }

    @Override
    public BatchStatus startBatch() throws InterruptedException {
        waitForNumberOfSeconds(sleepTime);
        BatchStatus batchStatus = new BatchStatus();

        batchStatus.setStatus(Status.SUCCESS);
        batchStatus.setMessage("Batch job went just fine.");

        return batchStatus;
    }

    private void waitForNumberOfSeconds(int seconds) throws InterruptedException {
        TimeUnit.SECONDS.sleep(seconds);
    }
}
