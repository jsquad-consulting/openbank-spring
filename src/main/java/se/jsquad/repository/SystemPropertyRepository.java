package se.jsquad.repository;

import se.jsquad.entity.SystemProperty;

import java.util.List;

public interface SystemPropertyRepository {
    void persistSystemProperty(SystemProperty systemProperty);
    List<SystemProperty> findAllUniqueSystemProperties();
    void clearSecondaryLevelCache();
    void refreshSecondaryLevelCache();
}
