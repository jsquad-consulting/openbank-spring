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
import org.springframework.stereotype.Component;

@Component
@Aspect
public class LoggingAspect {
    @Around("within(se.jsquad..*)")
    public Object logMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        final Logger logger = LogManager.getLogger(joinPoint.getTarget().getClass().getName());
        Object returnValue;

        try {
            StringBuilder startMessageStringBuilder = new StringBuilder();

            startMessageStringBuilder.append(joinPoint.getSignature().getName());
            startMessageStringBuilder.append("(");

            Object[] args = joinPoint.getArgs();
            for (int i = 0; i < args.length; ++i) {
                startMessageStringBuilder.append(args[i]).append(",");
            }
            if (args.length > 0) {
                startMessageStringBuilder.deleteCharAt(startMessageStringBuilder.length() - 1);
            }

            startMessageStringBuilder.append(")");

            logger.log(Level.INFO, startMessageStringBuilder.toString());

            returnValue = joinPoint.proceed();

            StringBuilder endMessageStringBuilder = new StringBuilder();
            endMessageStringBuilder.append("Finish method ");
            endMessageStringBuilder.append(joinPoint.getSignature().getName());

            logger.log(Level.INFO, endMessageStringBuilder.toString());
        } catch (Throwable e) {
            StringBuilder errorMessageStringBuilder = new StringBuilder();
            logger.log(Level.ERROR, errorMessageStringBuilder.toString(), e);

            throw e;
        }

        return returnValue;
    }
}
