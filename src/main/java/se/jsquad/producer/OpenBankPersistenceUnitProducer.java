package se.jsquad.producer;

import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Named
public class OpenBankPersistenceUnitProducer {
    @PersistenceContext(unitName = "entityManagerFactoryOpenBank")
    private EntityManager entityManager;

    protected EntityManager getEntityManager() {
        return entityManager;
    }
}
