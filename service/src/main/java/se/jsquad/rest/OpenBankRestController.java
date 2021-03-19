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

import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import se.jsquad.batch.status.BatchStatus;
import se.jsquad.business.OpenBankService;

import java.util.concurrent.Future;

@Controller("openBankRestController")
@RequestMapping(path = "/api")
public class OpenBankRestController {
    private Logger logger;
    private OpenBankService openBankService;

    public OpenBankRestController(Logger logger,
                                  OpenBankService openBankService) {
        this.logger = logger;
        this.openBankService = openBankService;
    }

    @GetMapping(value = "/openbank/start/slow/batch/mock", produces = {MediaType.APPLICATION_JSON_VALUE})
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
