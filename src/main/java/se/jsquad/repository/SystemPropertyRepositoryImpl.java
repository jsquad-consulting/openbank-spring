package se.jsquad.repository;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import se.jsquad.entity.SystemProperty;
import se.jsquad.producer.OpenBankPersistenceUnitProducerAbstract;

import javax.persistence.TypedQuery;
import java.util.List;

@Repository("systemPropertyRepositoryImpl")
@Qualifier("systemPropertyRepository")
public class SystemPropertyRepositoryImpl extends OpenBankPersistenceUnitProducerAbstract implements SystemPropertyRepository {
    private Logger logger;

    @Autowired
    private SystemPropertyRepositoryImpl(@Qualifier("logger") Logger logger) {
        logger.log(Level.INFO, "SystemPropertyRepositoryImpl(logger: {})", logger);
        this.logger = logger;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void persistSystemProperty(SystemProperty systemProperty) {
        logger.log(Level.INFO, "persistSystemProperty: systemProperty: {}", systemProperty);
        entityManager.persist(systemProperty);
    }

    @Override
    public List<SystemProperty> findAllUniqueSystemProperties() {
        logger.log(Level.INFO, "findAllUniqueSystemProperties() is being called and caching the secondary cache level"
                + " with SYSTEMPROPERTY entities.");

        TypedQuery<SystemProperty> query = entityManager.createNamedQuery(SystemProperty
                .FIND_ALL_UNIQUE_SYSTEM_PROPERTIES, SystemProperty.class);

        return query.getResultList();
    }

    @Override
    public void clearSecondaryLevelCache() {
        logger.log(Level.INFO, "clearSecondaryLevelCache() method is called for clearing all of the SystemProperty " +
                "entities from the secondary level JPA cache.");
        entityManager.getEntityManagerFactory().getCache().evictAll();
    }

    @Override
    public void refreshSecondaryLevelCache() {
        logger.log(Level.INFO, "refreshSecondaryLevelCache() refreshing the secondary level cache for SYSTEMPROPERTY "
                + "entities.");
        clearSecondaryLevelCache();
        findAllUniqueSystemProperties();
    }
}
