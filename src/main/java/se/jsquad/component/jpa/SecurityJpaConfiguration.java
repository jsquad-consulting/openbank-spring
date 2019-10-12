package se.jsquad.component.jpa;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.jpa")
public class SecurityJpaConfiguration extends JpaConfiguration {
}