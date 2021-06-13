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

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import se.jsquad.api.JunkRest;
import se.jsquad.api.client.JunkApi;
import se.jsquad.component.header.RequestHeaderController;

@RestController
@RequestHeaderController
public class JunkRestController implements JunkRest {
    @Override
    public ResponseEntity<JunkApi> getJunkInformation(String junkInformation) {
        return ResponseEntity.ok(new JunkApi());
    }
}