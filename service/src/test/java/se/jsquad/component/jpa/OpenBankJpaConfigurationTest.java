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

package se.jsquad.component.jpa;

import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OpenBankJpaConfigurationTest {
    @Test
    public void testJpaConfigurationFieldConstraints() {
        // Given
        JpaConfiguration openBankJpaConfiguration = new OpenBankJpaConfiguration();

        // When
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<JpaConfiguration>> constraintViolationSet = validator
                .validate(openBankJpaConfiguration);

        // Then
        assertEquals(3, constraintViolationSet.size());

        ConstraintViolation<JpaConfiguration> jpaConfigurationConstraintViolation = constraintViolationSet.stream()
                .filter(jpaConfigurationConstraintViolation1 -> "must not be null"
                        .equals(jpaConfigurationConstraintViolation1.getMessage())
                        && "databasePlatform".equals(jpaConfigurationConstraintViolation1.getPropertyPath()
                        .toString())).findFirst().get();

        assertEquals("must not be null", jpaConfigurationConstraintViolation.getMessage());
        assertEquals("databasePlatform", jpaConfigurationConstraintViolation.getPropertyPath().toString());

        jpaConfigurationConstraintViolation = constraintViolationSet.stream()
                .filter(jpaConfigurationConstraintViolation1 -> "must not be empty"
                        .equals(jpaConfigurationConstraintViolation1.getMessage())).findFirst().get();

        assertEquals("must not be empty", jpaConfigurationConstraintViolation.getMessage());
        assertEquals("databasePlatform", jpaConfigurationConstraintViolation.getPropertyPath().toString());

        jpaConfigurationConstraintViolation = constraintViolationSet.stream()
                .filter(jpaConfigurationConstraintViolation1 -> "must not be null"
                        .equals(jpaConfigurationConstraintViolation1.getMessage())
                        && "entityValidation".equals(jpaConfigurationConstraintViolation1.getPropertyPath()
                        .toString()))
                .findFirst().get();

        assertEquals("must not be null", jpaConfigurationConstraintViolation.getMessage());
        assertEquals("entityValidation", jpaConfigurationConstraintViolation.getPropertyPath().toString());

        // Given
        openBankJpaConfiguration.setDatabasePlatform("test");
        openBankJpaConfiguration.setEntityValidation("ore");

        // When
        constraintViolationSet = validator.validate(openBankJpaConfiguration);

        // Then
        assertEquals(1, constraintViolationSet.size());

        jpaConfigurationConstraintViolation = constraintViolationSet.stream()
                .filter(jpaConfigurationConstraintViolation1 -> "must match \"^validate$\""
                        .equals(jpaConfigurationConstraintViolation1.getMessage())).findFirst().get();

        assertEquals("entityValidation", jpaConfigurationConstraintViolation.getPropertyPath()
                .toString());

        // Given
        openBankJpaConfiguration.setDatabasePlatform("test");
        openBankJpaConfiguration.setEntityValidation("validate");

        // When
        constraintViolationSet = validator.validate(openBankJpaConfiguration);

        // Then
        assertEquals(0, constraintViolationSet.size());
    }
}