package se.jsquad.component.webclient;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "world.webclient")
public class WorldWebClientConfiguration extends WebClientConfiguration {
}