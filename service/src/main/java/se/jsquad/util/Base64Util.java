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

package se.jsquad.util;

import org.springframework.stereotype.Component;
import se.jsquad.exception.Base64RuntimeException;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.regex.Pattern;

import static se.jsquad.interceptor.RequestHeaderInterceptor.X_AUTHORIZATION_HEADER_NAME;

@Component
public class Base64Util {
    public String decodeEncodedToken(final String encodedToken) {
        try {
            return new String(Base64.getDecoder().decode(encodedToken), StandardCharsets.UTF_8)
                .replaceAll("(\\r|\\n)", "");
        } catch (Exception e) {
            var message = X_AUTHORIZATION_HEADER_NAME + " value must be a Base64 encoded string.";
            
            throw new Base64RuntimeException(message, e);
        }
    }
    
    public String getBasicAuthenticationName(final String encodedToken) {
        var pattern = Pattern.compile("(\\w*):.*", Pattern.CASE_INSENSITIVE);
        
        var matcher = pattern.matcher(decodeEncodedToken(encodedToken).trim());
        
        if (matcher.matches()) {
            return matcher.group(1);
        } else {
            return "";
        }
    }
}
