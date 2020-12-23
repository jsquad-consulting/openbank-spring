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

package se.jsquad.component.jpa;

import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Validated
public class JpaConfiguration {
    @NotNull
    @NotEmpty
    private String databasePlatform;

    @NotNull
    @Pattern(regexp = "^validate$")
    private String entityValidation;

    private String databaseAction;

    private String secondaryLevelCache;

    private String cacheRegionFactory;

    public String getDatabasePlatform() {
        return databasePlatform;
    }

    public void setDatabasePlatform(String databasePlatform) {
        this.databasePlatform = databasePlatform;
    }

    public String getEntityValidation() {
        return entityValidation;
    }

    public void setEntityValidation(String entityValidation) {
        this.entityValidation = entityValidation;
    }

    public String getDatabaseAction() {
        return databaseAction;
    }

    public void setDatabaseAction(String databaseAction) {
        this.databaseAction = databaseAction;
    }

    public String getSecondaryLevelCache() {
        return secondaryLevelCache;
    }

    public void setSecondaryLevelCache(String secondaryLevelCache) {
        this.secondaryLevelCache = secondaryLevelCache;
    }

    public String getCacheRegionFactory() {
        return cacheRegionFactory;
    }

    public void setCacheRegionFactory(String cacheRegionFactory) {
        this.cacheRegionFactory = cacheRegionFactory;
    }
}
