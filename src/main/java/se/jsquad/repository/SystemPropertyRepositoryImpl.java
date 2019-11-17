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

package se.jsquad.repository;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import se.jsquad.entity.SystemProperty;
import se.jsquad.producer.OpenBankPersistenceUnitProducer;

import javax.persistence.TypedQuery;
import java.util.List;

@Repository("systemPropertyRepositoryImpl")
@Qualifier("systemPropertyRepository")
public class SystemPropertyRepositoryImpl extends OpenBankPersistenceUnitProducer implements SystemPropertyRepository {
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
