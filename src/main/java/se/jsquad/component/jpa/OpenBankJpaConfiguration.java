package se.jsquad.component.jpa;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "openbank.jpa")
public class OpenBankJpaConfiguration extends JpaConfiguration {
}