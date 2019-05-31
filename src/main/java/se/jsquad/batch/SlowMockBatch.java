package se.jsquad.batch;

import se.jsquad.batch.status.BatchStatus;

public interface SlowMockBatch {
    BatchStatus startBatch() throws InterruptedException;
}
