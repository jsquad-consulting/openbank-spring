package se.jsquad.component.database;

import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OpenBankDatabaseConfigurationTest {
    @Test
    public void testDatabaseConfigurationFieldConstraints() {
        // Given
        DatabaseConfiguration openBankDatabaseConfiguration = new OpenBankDatabaseConfiguration();

        // When
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<DatabaseConfiguration>> constraintViolationSet = validator
                .validate(openBankDatabaseConfiguration);

        // Then
        assertEquals(8, constraintViolationSet.size());

        Optional<ConstraintViolation<DatabaseConfiguration>> constraintViolationOptional =
                constraintViolationSet.stream()
                        .filter(databaseConfigurationConstraintViolation1 -> "must not be null"
                                .equals(databaseConfigurationConstraintViolation1.getMessage())
                                && "url".equals(databaseConfigurationConstraintViolation1.getPropertyPath()
                                .toString()))
                        .findFirst();

        assertTrue(constraintViolationOptional.isPresent());


        constraintViolationOptional =
                constraintViolationSet.stream()
                        .filter(databaseConfigurationConstraintViolation1 -> "must not be empty"
                                .equals(databaseConfigurationConstraintViolation1.getMessage())
                                && "url".equals(databaseConfigurationConstraintViolation1.getPropertyPath()
                                .toString())).findFirst();

        assertTrue(constraintViolationOptional.isPresent());

        constraintViolationOptional =
                constraintViolationSet.stream()
                        .filter(databaseConfigurationConstraintViolation1 -> "must not be null"
                                .equals(databaseConfigurationConstraintViolation1.getMessage())
                                && "username".equals(databaseConfigurationConstraintViolation1.getPropertyPath()
                                .toString())).findFirst();

        assertTrue(constraintViolationOptional.isPresent());


        constraintViolationOptional =
                constraintViolationSet.stream()
                        .filter(databaseConfigurationConstraintViolation1 -> "must not be empty"
                                .equals(databaseConfigurationConstraintViolation1.getMessage())
                                && "username".equals(databaseConfigurationConstraintViolation1.getPropertyPath()
                                .toString())).findFirst();

        assertTrue(constraintViolationOptional.isPresent());

        constraintViolationOptional =
                constraintViolationSet.stream()
                        .filter(databaseConfigurationConstraintViolation1 -> "must not be null".equals(
                                databaseConfigurationConstraintViolation1.getMessage()) &&
                                "password".equals(databaseConfigurationConstraintViolation1.getPropertyPath()
                                        .toString())).findFirst();

        assertTrue(constraintViolationOptional.isPresent());


        constraintViolationOptional =
                constraintViolationSet.stream()
                        .filter(databaseConfigurationConstraintViolation1 -> "must not be empty"
                                .equals(databaseConfigurationConstraintViolation1.getMessage())
                                && "password".equals(databaseConfigurationConstraintViolation1.getPropertyPath()
                                .toString())).findFirst();

        assertTrue(constraintViolationOptional.isPresent());

        constraintViolationOptional =
                constraintViolationSet.stream()
                        .filter(databaseConfigurationConstraintViolation1 -> "must not be null"
                                .equals(databaseConfigurationConstraintViolation1.getMessage())
                                && "driverclassname".equals(databaseConfigurationConstraintViolation1.getPropertyPath()
                                .toString())).findFirst();

        assertTrue(constraintViolationOptional.isPresent());


        constraintViolationOptional =
                constraintViolationSet.stream()
                        .filter(databaseConfigurationConstraintViolation1 -> "must not be empty"
                                .equals(databaseConfigurationConstraintViolation1.getMessage())
                                && "driverclassname".equals(databaseConfigurationConstraintViolation1.getPropertyPath()
                                .toString()))
                        .findFirst();

        assertTrue(constraintViolationOptional.isPresent());

        // Given
        openBankDatabaseConfiguration.setDriverclassname("test");
        openBankDatabaseConfiguration.setPassword("test");
        openBankDatabaseConfiguration.setUsername("test");
        openBankDatabaseConfiguration.setUrl("test");

        // When and then
        assertEquals(0, validator.validate(openBankDatabaseConfiguration).size());
    }
}