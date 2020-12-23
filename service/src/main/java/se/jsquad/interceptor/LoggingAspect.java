/*
 * Copyright 2019 JSquad AB
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

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static java.util.List.of;

@Component
@Aspect
public class LoggingAspect {
    @Pointcut("within(se.jsquad..*)")
    private void anyJsquadPackage() {
    }
    
    @Pointcut("!within(se.jsquad.interceptor.*)")
    private void avoidInterceptorPackage() {
    }
    
    @Around("anyJsquadPackage() && avoidInterceptorPackage()")
    public Object logEntranceAndExitToAllConsumerPowerPricingMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        final Logger logger = LogManager.getLogger(joinPoint.getTarget().getClass().getName());
        Object returnValue;
        
        try {
            logger.info(generateStartMethodMessage(joinPoint.getSignature().getName(), joinPoint.getArgs()));
            
            returnValue = joinPoint.proceed();
            
            logger.info(generateEndMethodMessage(joinPoint.getSignature().getName()));
            
        } catch (Throwable throwable) {
            logger.error(throwable.getMessage(), throwable);
            throw throwable;
        }
        
        return returnValue;
    }
    
    private String generateStartMethodMessage(String joinPointSignatureName, Object[] joinPointArguments) {
        StringBuilder startMethodMessage = new StringBuilder();
        
        startMethodMessage
            .append(joinPointSignatureName)
            .append("(")
            .append(generateArgumentsMessage(joinPointArguments))
            .append(")");
        
        return startMethodMessage.toString();
    }
    
    
    private String generateArgumentsMessage(Object[] joinPointArguments) {
        StringBuilder argumentsMessage = new StringBuilder();
    
        for (Object joinPointArgument : joinPointArguments) {
            argumentsMessage.append(joinPointArgument).append(",");
        }
        
        return argumentsMessageToString(argumentsMessage);
    }
    
    private String argumentsMessageToString(StringBuilder argumentsMessage) {
        if (argumentsMessage.length() > 0) {
            return argumentsMessage.substring(0, argumentsMessage.length() - 1);
        } else {
            return argumentsMessage.toString();
        }
    }
    
    private String generateEndMethodMessage(String joinPointSignatureName) {
        StringBuilder endMethodMessage = new StringBuilder();
        endMethodMessage
            .append("Finish method ")
            .append(joinPointSignatureName);
        
        return endMethodMessage.toString();
    }
}
