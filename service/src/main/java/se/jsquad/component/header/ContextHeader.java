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

package se.jsquad.component.header;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static java.lang.String.format;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static org.springframework.web.context.request.RequestContextHolder.getRequestAttributes;

@Component
public class ContextHeader {
    private final ApplicationContext applicationContext;
    
    public ContextHeader(final ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
    
    public String getBasicAuthenticationNameWithLogFormat() {
        Optional<RequestHeader> requestHeader = getRequestHeader();
        if (requestHeader.isPresent() && StringUtils.isNotEmpty(requestHeader.get().getBasicAuthenticationName())) {
            return format("CLIENT_NAME: %s,", requestHeader.get().getBasicAuthenticationName());
        }
        
        return "";
    }
    
    public String getCorrelationIdWithLogFormat() {
        Optional<RequestHeader> requestHeader = getRequestHeader();
        if (requestHeader.isPresent() && StringUtils.isNotEmpty(requestHeader.get().getCorrelationId())) {
            String correlationId = requestHeader.get().getCorrelationId();
            
            return format("CORRELATION_ID: %s", correlationId);
        }
        
        return "";
    }
    
    public String getCorrelationId() {
        Optional<RequestHeader> requestHeader = getRequestHeader();
        if (requestHeader.isPresent() && StringUtils.isNotEmpty(requestHeader.get().getCorrelationId())) {
            String correlationId = requestHeader.get().getCorrelationId();
            
            return correlationId;
        }
        
        return "";
    }
    
    private Optional<RequestHeader> getRequestHeader() {
        if (getRequestAttributes() != null) {
            return ofNullable(applicationContext.getBean(RequestHeader.class));
        }
        
        return empty();
    }
}