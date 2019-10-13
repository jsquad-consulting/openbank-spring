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
        Set<ConstraintViolation<JpaConfiguration>> constraintViolationSet = validator.validate(openBankJpaConfiguration);

        // Then
        assertEquals(3, constraintViolationSet.size());

        ConstraintViolation<JpaConfiguration> jpaConfigurationConstraintViolation = constraintViolationSet.stream()
                .filter(jpaConfigurationConstraintViolation1 -> "must not be null"
                        .equals(jpaConfigurationConstraintViolation1.getMessage())
                        && "databasePlatform".equals(jpaConfigurationConstraintViolation1.getPropertyPath().toString()))
                .findFirst().get();

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
                        && "entityValidation".equals(jpaConfigurationConstraintViolation1.getPropertyPath().toString()))
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

        assertEquals("entityValidation", jpaConfigurationConstraintViolation.getPropertyPath().toString());

        // Given
        openBankJpaConfiguration.setDatabasePlatform("test");
        openBankJpaConfiguration.setEntityValidation("validate");

        // When
        constraintViolationSet = validator.validate(openBankJpaConfiguration);

        // Then
        assertEquals(0, constraintViolationSet.size());
    }
}