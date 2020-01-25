package se.jsquad.configuration;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import org.springframework.context.annotation.Configuration;
import se.jsquad.actuator.DeepSystemStatusIndicator;
import se.jsquad.actuator.ShallowSystemStatusIndicator;

@Configuration
class HealthMetricsConfiguration {
    public HealthMetricsConfiguration(ShallowSystemStatusIndicator shallowSystemStatusIndicator,
                                      DeepSystemStatusIndicator deepSystemStatusIndicator,
                                      MeterRegistry meterRegistry) {

        meterRegistry.gauge("shallow-system-status", Tags.of("status", "up"), shallowSystemStatusIndicator,
                s -> "UP".equals(s.getShallowSystemStatus().getBody().getStatus().value()) ? 1 : 0);

        meterRegistry.gauge("deep-system-status", Tags.of("status", "up"), deepSystemStatusIndicator,
                d -> "UP".equals(d.getDeepSystemStatus().getBody().getStatus().value()) ? 1 : 0);
    }
}