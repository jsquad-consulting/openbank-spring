package se.jsquad.component.database;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.datasource")
public class SecurityDatabaseConfiguration extends DatabaseConfiguration {
}