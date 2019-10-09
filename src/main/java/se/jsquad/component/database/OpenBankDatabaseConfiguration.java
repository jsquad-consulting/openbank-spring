package se.jsquad.component.database;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "openbank.datasource")
public class OpenBankDatabaseConfiguration extends DatabaseConfiguration {
}