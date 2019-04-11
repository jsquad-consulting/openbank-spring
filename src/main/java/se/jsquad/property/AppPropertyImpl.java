package se.jsquad.property;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component("appProperty")
@PropertySource("classpath:/META-INF/property/app.properties")
public class AppPropertyImpl implements AppProperty {
    private Logger logger;

    @Autowired
    private AppPropertyImpl(@Qualifier("logger") Logger logger) {
        this.logger = logger;
        this.logger.log(Level.INFO, "AppPropertyImpl(logger: {})", logger);
    }

    @Value("${app.version}")
    private String version;

    @Value("${app.name}")
    private String name;

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

    public void setVersion(String version) {
        this.version = version;
    }
}
