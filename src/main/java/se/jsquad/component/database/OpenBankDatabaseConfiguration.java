package se.jsquad.component.database;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "openbank.datasource")
public class OpenBankDatabaseConfiguration extends DatabaseConfiguration {
}