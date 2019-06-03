package se.jsquad.profile;

import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
@Profile({"dev", "default"})
public class DevDataSourceConfig {
    @Bean(name = "dbProds")
    public PropertiesFactoryBean getPropertiesFactoryBean() {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("/META-INF/property/db_dev.properties"));

        return propertiesFactoryBean;
    }
}
