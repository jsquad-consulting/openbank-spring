/*
 * Copyright 2021 JSquad AB
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

package se.jsquad.actuator;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import se.jsquad.api.health.DeepSystemStatusResponse;
import se.jsquad.api.health.Dependencies;
import se.jsquad.api.health.HealthStatus;

@Component
@Endpoint(id = "deephealth")
public class DeepSystemStatusIndicator {
    private Gauge gaugeDeepHealth;
    private Gauge gaugeService;
    private Gauge gaugeOpenBankDatabase;
    private Gauge gaugeSecurityDatabase;

    public DeepSystemStatusIndicator(ShallowSystemStatusIndicator shallowSystemStatusIndicator,
                                     @Qualifier("openbankDatabaseHealthIndicator")
                                             HealthIndicator openbankDatabaseHealthIndicator,
                                     @Qualifier("securityDatabaseHealthIndicator")
                                             HealthIndicator securityDatabaseHealthIndicator,
                                     MeterRegistry meterRegistry) {
        gaugeDeepHealth = addGaugeDescription(Gauge.builder("deep_health", this,
                deepSystemStatusIndicator -> "UP".equals(deepSystemStatusIndicator.getDeepSystemStatus()
                        .getBody().getStatus().value()) ? 1 : 0),
            "Deep health of this service and it's dependencies", meterRegistry);

        gaugeService = addGaugeDescription(Gauge.builder("deep_health_service", shallowSystemStatusIndicator,
                s -> "UP".equals(s.getShallowSystemStatus().getBody().getStatus().value()) ? 1 : 0),
            "Health of just this service", meterRegistry);

        gaugeOpenBankDatabase = addGaugeDescription(Gauge.builder("deep_health_openbank_database",
            openbankDatabaseHealthIndicator, o -> "UP".equals(o.health().getStatus().getCode()) ? 1 : 0),
            "Health of the openbank database", meterRegistry);

        gaugeSecurityDatabase = addGaugeDescription(Gauge.builder("deep_health_security_database",
            securityDatabaseHealthIndicator, s -> "UP".equals(s.health().getStatus().getCode()) ? 1 : 0),
                "Health of the security database", meterRegistry);
        
        gaugeDeepHealth.measure();
        gaugeService.measure();
        gaugeOpenBankDatabase.measure();
        gaugeSecurityDatabase.measure();
    }

    @ReadOperation
    public ResponseEntity<DeepSystemStatusResponse> getDeepSystemStatus() {
        return ResponseEntity.ok(checkDeepSystemStatus());
    }

    private DeepSystemStatusResponse checkDeepSystemStatus() {
        DeepSystemStatusResponse deepSystemStatusResponse = new DeepSystemStatusResponse();
        
        deepSystemStatusResponse.setService(setHealthStatus(gaugeService));

        Dependencies dependencies = new Dependencies();
        deepSystemStatusResponse.setDependencies(dependencies);

        dependencies.setSecurityDb(setHealthStatus(gaugeSecurityDatabase));
        dependencies.setOpenbankDb(setHealthStatus(gaugeOpenBankDatabase));
        
        if (HealthStatus.UP.equals(dependencies.getOpenbankDb())
            && HealthStatus.UP.equals(dependencies.getSecurityDb())
            && HealthStatus.UP.equals(deepSystemStatusResponse.getService())) {
            deepSystemStatusResponse.setStatus(HealthStatus.UP);
        } else {
            deepSystemStatusResponse.setStatus(HealthStatus.DOWN);
        }

        return deepSystemStatusResponse;
    }
    
    private <T> Gauge addGaugeDescription(Gauge.Builder<T> gaugeBuilder, String description, MeterRegistry meterRegistry) {
        return gaugeBuilder.strongReference(true)
            .description(description)
            .tags(Tags.of("status", "up"))
            .register(meterRegistry);
    }
    
    private HealthStatus setHealthStatus(Gauge gauge) {
        return gauge.value() == 1.0 ? HealthStatus.UP : HealthStatus.DOWN;
    }
}