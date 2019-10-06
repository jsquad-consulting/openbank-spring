package se.jsquad.producer;

import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Named
public class SecurityPersistenceUnitProducer {
    @PersistenceContext(unitName = "entityManagerFactorySecurity")
    private EntityManager entityManager;

    protected EntityManager getEntityManager() {
        return entityManager;
    }
}