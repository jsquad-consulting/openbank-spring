package se.jsquad.client;

import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import se.jsquad.client.info.WorldApiResponse;
import se.jsquad.exception.WebClientException;

import javax.inject.Named;

@Named
public class WorldApiClient {
    private WebClient webClient;
    private Logger logger;

    public WorldApiClient(Logger logger, WebClient webClient) {
        this.webClient = webClient;
        this.logger = logger;
    }

    public WorldApiResponse getWorldApiResponse() {
        return webClient.get().uri("/api/get/hello/world")
                .accept(MediaType.APPLICATION_JSON).retrieve()
                .bodyToMono(WorldApiResponse.class)
                .doOnError(throwable -> {
                    logger.error(throwable.getMessage(), throwable);
                    throw new WebClientException("World Api Client not available at this time.");
                })
                .block();
    }
}