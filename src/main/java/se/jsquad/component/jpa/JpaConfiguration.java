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
