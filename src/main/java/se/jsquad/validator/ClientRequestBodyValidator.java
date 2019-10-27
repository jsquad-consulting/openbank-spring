package se.jsquad.validator;

import se.jsquad.client.info.ClientRequest;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ClientRequestBodyValidator implements ConstraintValidator<ClientRequestBodyConstraint, ClientRequest> {
    @Override
    public void initialize(ClientRequestBodyConstraint constraintAnnotation) {}

    @Override
    public boolean isValid(ClientRequest clientRequest, ConstraintValidatorContext constraintValidatorContext) {
        if (clientRequest == null) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate("Client request can't be null.")
                    .addConstraintViolation();
            return false;
        } else if (clientRequest.getPersonIdentificationNumber() == null || !clientRequest
                .getPersonIdentificationNumber().matches("[0-9]{12}")) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate("Person identification number must" +
                    " be twelve digits.").addConstraintViolation();
            return false;

        } else {
            return true;
        }
    }
}