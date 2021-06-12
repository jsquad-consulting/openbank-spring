/*
 * Copyright 2021 JSquad AB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package se.jsquad.batch;

import org.springframework.stereotype.Service;
import se.jsquad.api.batch.BatchStatus;
import se.jsquad.api.batch.Status;
import se.jsquad.property.AppPropertyConfiguration;

import java.util.concurrent.TimeUnit;

@Service
public class SlowMockBatchImpl implements SlowMockBatch {
    private int sleepTime;

    private AppPropertyConfiguration appPropertyConfiguration;

    public SlowMockBatchImpl(AppPropertyConfiguration appPropertyConfiguration) {
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
