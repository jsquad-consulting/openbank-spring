/*
 * Copyright 2020 JSquad AB
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

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import se.jsquad.exception.WebClientException;
import se.jsquad.health.check.Deep;
import se.jsquad.health.check.HealthStatus;
import se.jsquad.health.check.SystemStatusResponse;

import java.sql.Connection;

@Component
@Endpoint(id = "system-status")
public class SystemStatus {
    private Logger logger;

    private JdbcTemplate openBankJdbcTemplate;
    private JdbcTemplate securityJdbcTemplate;

    private WebClient webClient;

    public SystemStatus(Logger logger,
                        @Qualifier("InternalApiWebClient") WebClient webClient,
                        @Qualifier("openBankJdbcTemplate") JdbcTemplate openBankJdbcTemplate,
                        @Qualifier("securityJdbcTemplate") JdbcTemplate securityJdbcTemplate) {
        this.logger = logger;
        this.openBankJdbcTemplate = openBankJdbcTemplate;
        this.securityJdbcTemplate = securityJdbcTemplate;
        this.webClient = webClient;
    }

    @ReadOperation
    public ResponseEntity<SystemStatusResponse> getSystemStatus() {
        return ResponseEntity.ok(checkSystemStatus());
    }

    private SystemStatusResponse checkSystemStatus() {
        SystemStatusResponse systemStatusResponse = new SystemStatusResponse();

        systemStatusResponse.setStatus(HealthStatus.DOWN);

        Deep deep = new Deep();
        systemStatusResponse.setDeep(deep);

        deep.setSecurityDb(HealthStatus.DOWN);
        deep.setOpenbankDb(HealthStatus.DOWN);

        try (Connection connection = openBankJdbcTemplate.getDataSource().getConnection()) {
            deep.setOpenbankDb(HealthStatus.UP);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        try (Connection connection = securityJdbcTemplate.getDataSource().getConnection()) {
            deep.setSecurityDb(HealthStatus.UP);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        if (HealthStatus.UP.equals(deep.getOpenbankDb()) && HealthStatus.UP.equals(deep.getSecurityDb())) {
            systemStatusResponse.setStatus(HealthStatus.UP);
        }

        return systemStatusResponse;
    }

    @Scheduled(cron = "${system.health.check.interval}")
    void internalRestClientHealthCheck() {
        webClient.get().uri("/actuator/system-status")
                .accept(MediaType.APPLICATION_JSON).retrieve()
                .bodyToMono(SystemStatusResponse.class)
                .doOnError(throwable -> {
                    logger.error(throwable.getMessage(), throwable);
                    throw new WebClientException("Can't reach the system-status actuator endpoint at this time.");
                })
                .block();
    }
}