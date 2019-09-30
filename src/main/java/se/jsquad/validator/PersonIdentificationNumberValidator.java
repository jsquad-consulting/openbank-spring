package se.jsquad.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PersonIdentificationNumberValidator
        implements ConstraintValidator<PersonIdentificationNumberConstraint, String> {
    @Override
    public boolean isValid(String personIdentificationNumber, ConstraintValidatorContext constraintValidatorContext) {
        return personIdentificationNumber != null && !personIdentificationNumber.isEmpty()
                && personIdentificationNumber.matches("[0-9]{12}");
    }
}