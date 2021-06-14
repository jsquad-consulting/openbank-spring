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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.logging.ConditionEvaluationReportLoggingListener;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Execution(ExecutionMode.SAME_THREAD)
class BasicAuthValidatorApplicationExceptionTest {
    @Test
    void testApplicationStartupFailure() {
        ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withInitializer(new ConditionEvaluationReportLoggingListener(LogLevel.DEBUG))
            .withConfiguration(AutoConfigurations.of(BasicAuthValidator.class))
            .withPropertyValues("service.basic.auth.map.client1.token=,password2");
        
        contextRunner
            .run(context -> {
                final BeanCreationException beanCreationException = (BeanCreationException) context
                    .getStartupFailure();
                assertTrue(beanCreationException.getMessage()
                    .contains("BasicAuthMapException: The basic auth client map with related clients " +
                        "is not properly setup. The pattern is 'service.basic.auth.map.client1.token: " +
                        "'password1,password2'."), "Failed to assert error message "
                    + beanCreationException.getMessage());
            });
    }
    
    @Test
    void testApplicationStartupFailureVariant2() {
        ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withInitializer(new ConditionEvaluationReportLoggingListener(LogLevel.DEBUG))
            .withConfiguration(AutoConfigurations.of(BasicAuthValidator.class));
        
        contextRunner
            .run(context -> {
                final BeanCreationException beanCreationException = (BeanCreationException) context
                    .getStartupFailure();
                assertTrue(beanCreationException.getMessage()
                    .contains("BasicAuthMapException: The basic auth client map with related clients " +
                        "is not properly setup. The pattern is 'service.basic.auth.map.client1.token: " +
                        "'password1,password2'."), "Failed to assert error message "
                    + beanCreationException.getMessage());
            });
    }
}