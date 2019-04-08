package se.jsquad.property;

import org.springframework.beans.factory.annotation.Value;

import java.util.logging.Level;
import java.util.logging.Logger;

public class AppPropertyImpl implements AppProperty {
    private static final Logger logger = Logger.getLogger(AppPropertyImpl.class.getName());

    private String version;

    @Value("${app.name}")
    private String name;

    @Override
    public String getVersion() {
        logger.log(Level.FINE, "getVersion(), version: {0}", new Object[]{version});
        return version;
    }

    @Override
    public String getName() {
        logger.log(Level.FINE, "getName(), name: {0}", new Object[]{name});
        return name;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
