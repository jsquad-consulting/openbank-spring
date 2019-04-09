package se.jsquad.producer;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public abstract class EntityManagerProducerAbstract {
    @PersistenceContext
    protected EntityManager entityManager;
}
