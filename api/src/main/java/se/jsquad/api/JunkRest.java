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

package se.jsquad.api;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import se.jsquad.api.client.JunkApi;
import se.jsquad.constant.ApiConstants;

@Api(value = ApiConstants.OPENBANK_BASE_PATH, authorizations = {})
@RequestMapping(path = ApiConstants.OPENBANK_BASE_PATH)
@Validated
public interface JunkRest {
    @GetMapping(value = "/junk/info/test/{personIdentification}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(hidden = true)
    ResponseEntity<JunkApi> getJunkInformation(@PathVariable String junkInformation);
}