package se.jsquad.property;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:/META-INF/property/app.properties")
public class AppPropertyConfigurationImpl implements AppPropertyConfiguration {
    private Logger logger;

    public AppPropertyConfigurationImpl(Logger logger) {
        logger.log(Level.INFO, "AppPropertyImpl(logger: {})", logger);
        this.logger = logger;
    }

    @Value("${app.version}")
    private String version;

    @Value("${app.name}")
    private String name;

    @Value("${app.batch.sleep.time}")
    private Integer batchSleepTime;

    @Override
    public String getVersion() {
        logger.log(Level.INFO, "getVersion(), version: {}", version);

        return version;
    }

    @Override
    public String getName() {
        logger.log(Level.INFO, "getName(), name: {}", name);

        return name;
    }

    @Override
    public Integer getBatchSleepTime() {
        logger.log(Level.INFO, "getBatchSleepTime()");

        return batchSleepTime;
    }
}
