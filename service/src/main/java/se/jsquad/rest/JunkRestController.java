/*
 * Copyright 2020 JSquad AB
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

import io.swagger.v3.oas.annotations.Operation;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.jsquad.client.info.JunkApi;

@RestController
@RequestMapping(path = "/api")
public class JunkRestController {
    private Logger logger;

    public JunkRestController(Logger logger) {
        this.logger = logger;
    }

    @GetMapping(value = "/junk/info/test/{personIdentification}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(hidden = true)
    public ResponseEntity<JunkApi> getJunkInformation(@PathVariable String junkInformation) {

        return ResponseEntity.ok(new JunkApi());
    }
}