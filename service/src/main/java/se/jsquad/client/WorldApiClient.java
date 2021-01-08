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

package se.jsquad.client;

import javax.inject.Named;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import se.jsquad.client.info.WorldApiResponse;
import se.jsquad.exception.WebClientException;

@Named
public class WorldApiClient {
    private WebClient webClient;
    private Logger logger;

    public WorldApiClient(Logger logger, @Qualifier("WorldApiWebClient") WebClient webClient) {
        this.webClient = webClient;
        this.logger = logger;
    }

    public WorldApiResponse getWorldApiResponse() {
        return webClient.get().uri("/api/get/hello/world")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(httpStatus -> httpStatus.is5xxServerError(), clientResponse -> {
                    return Mono.error(new WebClientException("Webclient is not available at this time."));
                })
                .bodyToMono(WorldApiResponse.class)
                .block();
    }
}