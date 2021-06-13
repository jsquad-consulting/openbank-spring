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

package se.jsquad.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import se.jsquad.api.OpenBankRest;
import se.jsquad.api.batch.BatchStatus;
import se.jsquad.business.OpenBankService;
import se.jsquad.component.header.RequestHeaderController;

import java.util.concurrent.Future;

@RestController
@RequestHeaderController
public class OpenBankRestController implements OpenBankRest {
    private OpenBankService openBankService;

    public OpenBankRestController(OpenBankService openBankService) {
        this.openBankService = openBankService;
    }

    @Override
    public ResponseEntity getOpenBankBatchStatus() {
        try {
            Future<BatchStatus> batchStatusFuture = openBankService.startSlowBatch();

            return ResponseEntity.ok().body(batchStatusFuture.get());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.TEXT_PLAIN)
                    .body("System failure has occured");
        }
    }
}
