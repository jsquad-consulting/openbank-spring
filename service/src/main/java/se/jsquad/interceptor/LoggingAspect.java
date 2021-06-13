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

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import se.jsquad.component.header.ContextHeader;

import static java.util.stream.Stream.of;

@Component
@Aspect
public class LoggingAspect {
    private final ContextHeader contextHeader;
    
    public LoggingAspect(final ContextHeader contextHeader) {
        this.contextHeader = contextHeader;
    }
    
    @Pointcut("within(se.jsquad..*)")
    private void anyPackage() {
        // NO SONAR
    }
    
    @Pointcut("!within(se.jsquad.component.header.*) " +
        "&& !within(se.jsquad.interceptor.*)")
    private void avoidInterceptors() {
        // NO SONAR
    }
    
    @Around("anyPackage() && avoidInterceptors()")
    public Object logEntranceAndExitToAllMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        final var logger = LoggerFactory.getLogger(joinPoint.getTarget().getClass().getName());
        Object returnValue;
        
        final String startMethodMessage = generateStartMethodMessage(joinPoint.getSignature().getName(),
            joinPoint.getArgs());
        logger.info(startMethodMessage);
        
        returnValue = joinPoint.proceed();
        
        if (logger.isDebugEnabled()) {
            final String returnValueMessage = generateReturnValueMessage(joinPoint.getSignature().getName(),
                this.getObjectValueAsString(returnValue));
            logger.info(returnValueMessage);
        }
        
        final String endMethodMessage = generateEndMethodMessage(joinPoint.getSignature().getName());
        
        logger.info(endMethodMessage);
        
        return returnValue;
    }
    
    private String generateStartMethodMessage(String joinPointSignatureName, Object[] joinPointArguments) {
        return new StringBuilder()
            .append(joinPointSignatureName)
            .append("(")
            .append(contextHeader.getBasicAuthenticationNameWithLogFormat())
            .append(contextHeader.getCorrelationIdWithLogFormat())
            .append(generateArgumentsMessage(joinPointArguments))
            .append(")").toString();
    }
    
    private String generateArgumentsMessage(Object[] joinPointArguments) {
        var argumentsMessage = new StringBuilder();
        
        of(joinPointArguments)
            .forEach(argument -> argumentsMessage.append(",").append(argument));
        
        return argumentsMessage.toString();
    }
    
    private String generateEndMethodMessage(String joinPointSignatureName) {
        return new StringBuilder()
            .append("Finish method ")
            .append(joinPointSignatureName)
            .append("(")
            .append(contextHeader.getBasicAuthenticationNameWithLogFormat())
            .append(contextHeader.getCorrelationIdWithLogFormat())
            .append(")").toString();
    }
    
    
    private String generateReturnValueMessage(String joinPointSignatureName, String responseMessage) {
        return new StringBuilder()
            .append("Method ")
            .append(joinPointSignatureName)
            .append(" response")
            .append("(")
            .append(contextHeader.getBasicAuthenticationNameWithLogFormat())
            .append(contextHeader.getCorrelationIdWithLogFormat())
            .append(",")
            .append(responseMessage)
            .append(")").toString();
    }
    
    private String getObjectValueAsString(Object result) {
        String returnValue = null;
        if (null != result) {
            if (result.toString().endsWith("@" + Integer.toHexString(result.hashCode()))) {
                returnValue = ReflectionToStringBuilder.toString(result);
            } else {
                returnValue = result.toString();
            }
        }
        return returnValue;
    }
}