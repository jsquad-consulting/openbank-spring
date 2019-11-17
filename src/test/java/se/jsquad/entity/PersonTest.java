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

package se.jsquad.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.AnnotationConfigWebContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import se.jsquad.configuration.ApplicationConfiguration;
import se.jsquad.producer.OpenBankPersistenceUnitProducer;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = ApplicationConfiguration.class, loader = AnnotationConfigWebContextLoader.class)
@Transactional(propagation = Propagation.REQUIRED)
public class PersonTest {
    @Inject
    private OpenBankPersistenceUnitProducer openBankPersistenceUnitProducer;

    @Autowired
    private Validator validator;

    @Test
    public void testEntityManager() {
        assertNotNull(openBankPersistenceUnitProducer.getEntityManager());
    }

    @Test
    public void testPersistInvalidPerson() {
        // Given
        Person person = new Person();

        person.setFirstName("1234Hugo");
        person.setLastName("Karlsson1234");
        person.setPersonIdentification("19121212121");
        person.setClient(null);
        person.setMail("hugo@mail.");

        // When and then
        assertThrows(ConstraintViolationException.class, () -> {
            openBankPersistenceUnitProducer.getEntityManager().persist(person);
            openBankPersistenceUnitProducer.getEntityManager().flush();
        });
    }

    @Test
    public void testInValidPerson() {
        // Given
        Person person = new Person();

        person.setFirstName("1234Hugo");
        person.setLastName("Karlsson1234");
        person.setPersonIdentification("19121212121");
        person.setClient(null);
        person.setMail("hugo@mail.");

        // When
        Set<ConstraintViolation<Person>> constraintViolationSet = validator.validate(person);

        // Then
        assertEquals(4, constraintViolationSet.size());

        Optional<ConstraintViolation<Person>> constraintViolationOptional = constraintViolationSet.stream()
                .filter(cv -> "personIdentification".equals(cv.getPropertyPath().toString())).findFirst();

        ConstraintViolation<Person> constraintViolation = constraintViolationSet.iterator().next();

        if (constraintViolationOptional.isPresent()) {
            constraintViolation = constraintViolationOptional.get();
        }

        assertEquals("must match \"\\d{12}\"", constraintViolation.getMessage());
        assertEquals("personIdentification", constraintViolation.getPropertyPath().toString());

        constraintViolationOptional = constraintViolationSet.stream().filter(cv ->
                "lastName".equals(cv.getPropertyPath().toString())).findFirst();

        if (constraintViolationOptional.isPresent()) {
            constraintViolation = constraintViolationOptional.get();
        }

        assertEquals("must match \"^\\D*$\"", constraintViolation.getMessage());
        assertEquals("lastName", constraintViolation.getPropertyPath().toString());

        constraintViolationOptional = constraintViolationSet.stream().filter(cv ->
                "firstName".equals(cv.getPropertyPath().toString())).findFirst();

        if (constraintViolationOptional.isPresent()) {
            constraintViolation = constraintViolationOptional.get();
        }

        assertEquals("must match \"^\\D*$\"", constraintViolation.getMessage());
        assertEquals("firstName", constraintViolation.getPropertyPath().toString());

        constraintViolationOptional = constraintViolationSet.stream().filter(cv ->
                "mail".equals(cv.getPropertyPath().toString())).findFirst();

        if (constraintViolationOptional.isPresent()) {
            constraintViolation = constraintViolationOptional.get();
        }

        assertEquals("must match \"" + Person.MAIL_REGEXP + "\"", constraintViolation.getMessage());
        assertEquals("mail", constraintViolation.getPropertyPath().toString());
    }
}
