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

package se.jsquad.validator;

import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Component;
import se.jsquad.exception.BasicAuthMapException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Component
public class BasicAuthValidator {
    private static final String EXCEPTION_MESSAGE = "The basic auth client map with related clients is not " +
        "properly setup. The pattern is 'service.basic.auth.map.client1.token: 'password1,password2'.";
    
    private final Map<String, String> basicAuthMap;
    
    public BasicAuthValidator(final Environment environment) throws BasicAuthMapException {
        basicAuthMap = new HashMap<>();
    
        setupBasicAuthenticationMap((ConfigurableEnvironment) environment);
    
        if (basicAuthMap.isEmpty()) {
            throw new BasicAuthMapException(EXCEPTION_MESSAGE);
        } else {
            final Iterator<Map.Entry<String, String>> iterator = basicAuthMap.entrySet().iterator();
            while (iterator.hasNext()) {
                final Map.Entry<String, String> next = iterator.next();
                
                if (next.getValue().startsWith(",") || next.getValue().startsWith(";")) {
                    throw new BasicAuthMapException(EXCEPTION_MESSAGE);
                }
            }
        }
    }
    
    public boolean doesBasicAuthNameAndPasswordExist(final String clientName, final String password) {
        return basicAuthMap.containsKey(clientName + ".token")
            && Arrays.asList(basicAuthMap.get(clientName + ".token").split(","))
            .contains(password);
    }
    
    private void setupBasicAuthenticationMap(ConfigurableEnvironment environment) throws BasicAuthMapException {
        for (PropertySource<?> propertySource : environment.getPropertySources()) {
            if (propertySource instanceof EnumerablePropertySource) {
                for (String key : ((EnumerablePropertySource) propertySource).getPropertyNames()) {
                    if (key.startsWith("service.basic.auth.map")) {
                        String propertyValue = (String) propertySource.getProperty(key);
                        if (propertyValue == null) {
                            throw new BasicAuthMapException(EXCEPTION_MESSAGE);
                        }
                        
                        basicAuthMap.put(key.replace("service.basic.auth.map.", ""), propertyValue);
                    }
                }
            }
        }
    }
    
    Map<String, String> getBasicAuthMap() {
        return basicAuthMap;
    }
}
