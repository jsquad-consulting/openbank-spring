package se.jsquad.property;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:/META-INF/property/app.properties")
public class AppPropertyConfigurationImpl implements AppPropertyConfiguration {
    private Logger logger;

    public AppPropertyConfigurationImpl(Logger logger) {
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
        return version;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Integer getBatchSleepTime() {
        return batchSleepTime;
    }
}
