package se.jsquad.business;


import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import se.jsquad.client.WorldApiClient;
import se.jsquad.client.info.WorldApiResponse;

@Service
public class WebClientBusiness {
    private WorldApiClient worldApiClient;
    private Logger logger;

    public WebClientBusiness(Logger logger, WorldApiClient worldApiClient) {
        this.logger = logger;
        this.worldApiClient = worldApiClient;
    }

    public WorldApiResponse getWorldApiResponse() {
         return worldApiClient.getWorldApiResponse();
    }
}