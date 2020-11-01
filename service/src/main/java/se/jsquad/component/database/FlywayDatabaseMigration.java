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

package se.jsquad.component.database;


import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.flywaydb.core.api.configuration.ClassicConfiguration;
import org.flywaydb.core.api.configuration.Configuration;
import org.springframework.context.ApplicationContextException;

import javax.inject.Named;
import javax.sql.DataSource;

@Named
public class FlywayDatabaseMigration {
    private Logger logger = LogManager.getLogger(FlywayDatabaseMigration.class.getName());

    public void migrateToDatabase(String location, DataSource dataSource) {
        if (dataSource == null) {
            logger.log(Level.ERROR, "No data source found to execute the database migrations.");
            throw new ApplicationContextException("No data source found to execute the database migrations.");
        }

        Configuration configuration = new ClassicConfiguration();

        ((ClassicConfiguration) configuration).setBaselineOnMigrate(true);
        ((ClassicConfiguration) configuration).setDataSource(dataSource);
        ((ClassicConfiguration) configuration).setLocationsAsStrings(location);

        Flyway flyway = new Flyway(configuration);

        MigrationInfo migrationInfo = flyway.info().current();

        if (migrationInfo == null) {
            logger.log(Level.ERROR, "No existing database at the actual data source.");
        } else {
            logger.log(Level.INFO, String.format("At actual data source an existing database exist with the version " +
                    "%s and description %s", migrationInfo.getVersion(), migrationInfo.getDescription()));
        }

        flyway.migrate();

        logger.log(Level.INFO, String.format("Successfully migrated to database version %s",
                flyway.info().current().getVersion()));
    }
}