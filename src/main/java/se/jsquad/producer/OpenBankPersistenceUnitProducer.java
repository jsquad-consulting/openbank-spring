package se.jsquad.producer;

import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Named
public class OpenBankPersistenceUnitProducer {
    @PersistenceContext(unitName = "entityManagerFactoryOpenBank")
    protected EntityManager entityManager;

    public EntityManager getEntityManager() {
        return entityManager;
    }
}
