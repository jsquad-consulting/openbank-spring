/*
 * Copyright 2019 JSquad AB
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