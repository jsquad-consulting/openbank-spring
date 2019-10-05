package se.jsquad.repository;

import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import se.jsquad.entity.SystemProperty;
import se.jsquad.producer.OpenBankPersistenceUnitProducer;

import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class SystemPropertyRepositoryImpl extends OpenBankPersistenceUnitProducer implements SystemPropertyRepository {
    private Logger logger;

    public SystemPropertyRepositoryImpl(Logger logger) {
        this.logger = logger;
    }

    @Override
    @Transactional(transactionManager = "transactionManagerOpenBank", propagation = Propagation.REQUIRED)
    public void persistSystemProperty(SystemProperty systemProperty) {
        getEntityManager().persist(systemProperty);
    }

    @Override
    public List<SystemProperty> findAllUniqueSystemProperties() {
        TypedQuery<SystemProperty> query = getEntityManager().createNamedQuery(SystemProperty
                .FIND_ALL_UNIQUE_SYSTEM_PROPERTIES, SystemProperty.class);

        return query.getResultList();
    }

    @Override
    public void clearSecondaryLevelCache() {
        getEntityManager().getEntityManagerFactory().getCache().evictAll();
    }

    @Override
    public void refreshSecondaryLevelCache() {
        clearSecondaryLevelCache();
        findAllUniqueSystemProperties();
    }
}
