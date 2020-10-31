/*
 * Copyright 2020 JSquad AB
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

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import se.jsquad.client.info.ClientRequest;

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
        } else if (clientRequest.getClientData() == null ||
                clientRequest.getClientData().getPersonIdentificationNumber() == null || !clientRequest
                .getClientData().getPersonIdentificationNumber().matches("[0-9]{12}")) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate("Person identification number must" +
                    " be twelve digits.").addConstraintViolation();
            return false;

        } else {
            return true;
        }
    }
}