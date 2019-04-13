package se.jsquad.producer;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public abstract class OpenBankPersistenceUnitProducerAbstract {
    @PersistenceContext(unitName = "entityManagerFactory")
    protected EntityManager entityManager;
}
