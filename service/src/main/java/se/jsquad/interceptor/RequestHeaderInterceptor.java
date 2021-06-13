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

package se.jsquad.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import se.jsquad.component.header.RequestHeader;
import se.jsquad.component.header.RequestHeaderController;
import se.jsquad.exception.BasicAuthMapRuntimeException;
import se.jsquad.util.Base64Util;
import se.jsquad.validator.BasicAuthValidator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;
import java.util.regex.Pattern;

import static java.util.Optional.ofNullable;

@Component
@RequestScope
public class RequestHeaderInterceptor extends HandlerInterceptorAdapter {
    public static final String CORRELATION_ID_HEADER_NAME = "CORRELATION-ID";
    public static final String X_AUTHORIZATION_HEADER_NAME = "X-AUTHORIZATION";
    
    private final Base64Util base64Util;
    private final BasicAuthValidator basicAuthValidator;
    private final RequestHeader requestHeader;
    
    public RequestHeaderInterceptor(final RequestHeader requestHeader, final Base64Util base64Util,
                                    final BasicAuthValidator basicAuthValidator) {
        this.requestHeader = requestHeader;
        this.base64Util = base64Util;
        this.basicAuthValidator = basicAuthValidator;
    }
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (handler instanceof HandlerMethod && isRequestParameterControllerHandler((HandlerMethod) handler)) {
            requestHeader.setCorrelationId(getCorrelationId(request));
            setBasicAuthNameFromAuthorizationHeader(request);
        }
        
        return true;
    }
    
    private boolean isRequestParameterControllerHandler(HandlerMethod handlerMethod) {
        return handlerMethod.getBeanType().isAnnotationPresent(RequestHeaderController.class);
    }
    
    private void setBasicAuthNameFromAuthorizationHeader(HttpServletRequest request) {
        var pattern = Pattern.compile("(.*):(.*)", Pattern.CASE_INSENSITIVE);
        var matcher = pattern.matcher(ofNullable(base64Util.decodeEncodedToken(request
            .getHeader(X_AUTHORIZATION_HEADER_NAME))).orElse(""));
        
        if (!matcher.matches()) {
            throw new BasicAuthMapRuntimeException("The decoded Base64 " + X_AUTHORIZATION_HEADER_NAME + " header " +
                "value must be of pattern 'name:password'");
        }
        
        if (!basicAuthValidator.doesBasicAuthNameAndPasswordExist(matcher.group(1), matcher.group(2))) {
            var message = String.format("The client name %s with provided password does not exist",
                matcher.group(1));
            throw new BasicAuthMapRuntimeException(message);
        }
        
        requestHeader.setBasicAuthenticationName(base64Util.getBasicAuthenticationName(request
            .getHeader(X_AUTHORIZATION_HEADER_NAME)));
    }
    
    private String getCorrelationId(HttpServletRequest request) {
        return ofNullable(request.getHeader(CORRELATION_ID_HEADER_NAME)).orElse(UUID.randomUUID().toString());
    }
}
