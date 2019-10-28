package se.jsquad.configuration;


import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.connection.SingleConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.ws.config.annotation.EnableWs;
import se.jsquad.component.database.FlywayDatabaseMigration;
import se.jsquad.component.database.OpenBankDatabaseConfiguration;
import se.jsquad.component.database.SecurityDatabaseConfiguration;
import se.jsquad.component.jpa.OpenBankJpaConfiguration;
import se.jsquad.component.jpa.SecurityJpaConfiguration;
import se.jsquad.component.webclient.WorldWebClientConfiguration;

import javax.jms.ConnectionFactory;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.persistence.EntityManagerFactory;
import javax.validation.Validator;
import java.util.Collections;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@EnableScheduling
@EnableWs
@EnableAsync
@EnableJms
@ComponentScan(basePackages = {"se.jsquad"})
@EnableJpaRepositories(basePackages = {"se.jsquad.repository"})
@EnableAspectJAutoProxy
@EnableEncryptableProperties
@EnableConfigurationProperties(value = {OpenBankDatabaseConfiguration.class, SecurityDatabaseConfiguration.class,
        SecurityJpaConfiguration.class, OpenBankJpaConfiguration.class, WorldWebClientConfiguration.class})
public class ApplicationConfiguration {
    private Environment environment;
    private OpenBankDatabaseConfiguration openBankDatabaseConfiguration;
    private SecurityDatabaseConfiguration securityDatabaseConfiguration;
    private SecurityJpaConfiguration securityJpaConfiguration;
    private OpenBankJpaConfiguration openBankJpaConfiguration;
    private FlywayDatabaseMigration flywayDatabaseMigration;
    private WorldWebClientConfiguration worldWebClientConfiguration;
    private boolean migratedOpenBank = false;

    public ApplicationConfiguration(Environment environment, OpenBankDatabaseConfiguration
            openBankDatabaseConfiguration, SecurityDatabaseConfiguration securityDatabaseConfiguration,
                                    OpenBankJpaConfiguration openBankJpaConfiguration,
                                    SecurityJpaConfiguration securityJpaConfiguration,
                                    FlywayDatabaseMigration flywayDatabaseMigration,
                                    WorldWebClientConfiguration worldWebClientConfiguration) {
        this.environment = environment;
        this.openBankDatabaseConfiguration = openBankDatabaseConfiguration;
        this.securityDatabaseConfiguration = securityDatabaseConfiguration;
        this.flywayDatabaseMigration = flywayDatabaseMigration;
        this.securityJpaConfiguration = securityJpaConfiguration;
        this.openBankJpaConfiguration = openBankJpaConfiguration;
        this.worldWebClientConfiguration = worldWebClientConfiguration;
    }

    @Bean("logger")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    Logger getLogger(final InjectionPoint injectionPoint) {
        return LogManager.getLogger(injectionPoint.getMember().getDeclaringClass().getName());
    }

    @Bean("validator")
    Validator validator() {
        return new LocalValidatorFactoryBean();
    }

    @Bean
    @Qualifier("openBankJdbcTemplate")
    JdbcTemplate openBankJdbcTemplate() {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName(openBankDatabaseConfiguration.getDriverclassname());
        dataSourceBuilder.url(openBankDatabaseConfiguration.getUrl());
        dataSourceBuilder.username(openBankDatabaseConfiguration.getUsername());
        dataSourceBuilder.password(openBankDatabaseConfiguration.getPassword());

        if (!migratedOpenBank) {
            flywayDatabaseMigration.migrateToDatabase("db/migration/openbank", dataSourceBuilder.build());
            migratedOpenBank = true;
        }

        return new JdbcTemplate(dataSourceBuilder.build(), true);
    }

    @Bean("securityJdbcTemplate")
    JdbcTemplate securityJdbcTemplate() {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName(securityDatabaseConfiguration.getDriverclassname());
        dataSourceBuilder.url(securityDatabaseConfiguration.getUrl());
        dataSourceBuilder.username(securityDatabaseConfiguration.getUsername());
        dataSourceBuilder.password(securityDatabaseConfiguration.getPassword());

        return new JdbcTemplate(dataSourceBuilder.build(), true);
    }

    @Primary
    @Bean("entityManagerFactoryOpenBank")
    LocalContainerEntityManagerFactoryBean getLocalContainerEntityManagerFactoryBeanOpenBank(
            JpaVendorAdapter jpaVendorAdapter, @Qualifier("openBankJdbcTemplate") JdbcTemplate jdbcTemplate,
            @Value("#{dbProds.openbank_pu}") String persistenceUnitName) {
        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setJpaVendorAdapter(jpaVendorAdapter);
        factoryBean.setPersistenceUnitName(persistenceUnitName);
        factoryBean.setDataSource(jdbcTemplate.getDataSource());

        Properties properties = new Properties();

        properties.setProperty("hibernate.dialect", openBankJpaConfiguration.getDatabasePlatform());
        properties.setProperty("hibernate.hbm2ddl.auto", openBankJpaConfiguration.getEntityValidation());

        if (openBankJpaConfiguration.getSecondaryLevelCache() != null
                && !openBankJpaConfiguration.getSecondaryLevelCache().isEmpty()) {
            properties.setProperty("hibernate.cache.use_second_level_cache",
                    openBankJpaConfiguration.getSecondaryLevelCache());
        }

        if (openBankJpaConfiguration.getCacheRegionFactory() != null
                && !openBankJpaConfiguration.getCacheRegionFactory().isEmpty()) {
            properties.setProperty("hibernate.cache.region.factory_class",
                    openBankJpaConfiguration.getCacheRegionFactory());
        }

        if (openBankJpaConfiguration.getDatabaseAction() != null
                && !openBankJpaConfiguration.getDatabaseAction().isEmpty()) {
            properties.setProperty("javax.persistence.schema-generation.database.action",
                    openBankJpaConfiguration.getDatabaseAction());
        }

        factoryBean.setJpaProperties(properties);

        return factoryBean;
    }

    @Bean("entityManagerFactorySecurity")
    LocalContainerEntityManagerFactoryBean getLocalContainerEntityManagerFactoryBeanSecurity(
            JpaVendorAdapter jpaVendorAdapter, @Qualifier("securityJdbcTemplate") JdbcTemplate jdbcTemplate,
            @Value("#{dbProds.security_pu}") String persistenceUnitName) {
        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setJpaVendorAdapter(jpaVendorAdapter);
        factoryBean.setPersistenceUnitName(persistenceUnitName);
        factoryBean.setDataSource(jdbcTemplate.getDataSource());

        Properties properties = new Properties();

        properties.setProperty("hibernate.dialect", securityJpaConfiguration.getDatabasePlatform());
        properties.setProperty("hibernate.hbm2ddl.auto", securityJpaConfiguration.getEntityValidation());

        if (securityJpaConfiguration.getSecondaryLevelCache() != null
                && !securityJpaConfiguration.getSecondaryLevelCache().isEmpty()) {
            properties.setProperty("hibernate.cache.use_second_level_cache",
                    securityJpaConfiguration.getSecondaryLevelCache());
        }

        if (securityJpaConfiguration.getCacheRegionFactory() != null
                && !securityJpaConfiguration.getCacheRegionFactory().isEmpty()) {
            properties.setProperty("hibernate.cache.region.factory_class",
                    securityJpaConfiguration.getCacheRegionFactory());
        }

        if (securityJpaConfiguration.getDatabaseAction() != null
                && !securityJpaConfiguration.getDatabaseAction().isEmpty()) {
            properties.setProperty("javax.persistence.schema-generation.database.action",
                    securityJpaConfiguration.getDatabaseAction());
        }

        factoryBean.setJpaProperties(properties);

        return factoryBean;
    }

    @Bean("hibernateVendorAdapter")
    JpaVendorAdapter getJpaVendorAdapter() {
        return new HibernateJpaVendorAdapter();
    }

    @Bean("transactionManagerOpenBank")
    JpaTransactionManager getJpaTransactionManagerOpenBank(@Qualifier("entityManagerFactoryOpenBank")
                                                                   EntityManagerFactory entityManagerFactory) {
        return getJpaTransactionManager(entityManagerFactory);
    }

    @Bean("transactionManagerSecurity")
    JpaTransactionManager getJpaTransactionManagerSecurity(@Qualifier("entityManagerFactorySecurity")
                                                                   EntityManagerFactory entityManagerFactory) {
        return getJpaTransactionManager(entityManagerFactory);
    }

    private JpaTransactionManager getJpaTransactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setEntityManagerFactory(entityManagerFactory);
        return jpaTransactionManager;
    }

    @Bean("transactionTemplateOpenBank")
    TransactionTemplate getTransactionTemplateOpenBank(@Qualifier("transactionManagerOpenBank") JpaTransactionManager
                                                               jpaTransactionManager) {
        return getTransactionTemplate(jpaTransactionManager);
    }

    @Bean("transactionTemplateSecurity")
    TransactionTemplate getTransactionTemplateSecurity(@Qualifier("transactionManagerSecurity") JpaTransactionManager
                                                               jpaTransactionManager) {
        return getTransactionTemplate(jpaTransactionManager);
    }

    private TransactionTemplate getTransactionTemplate(JpaTransactionManager jpaTransactionManager) {
        TransactionTemplate transactionTemplate = new TransactionTemplate();
        transactionTemplate.setTransactionManager(jpaTransactionManager);
        transactionTemplate.setPropagationBehavior(Propagation.REQUIRED.value());

        return transactionTemplate;
    }

    @Bean("jmsTemplate")
    JmsTemplate getJmsTemplate(ConnectionFactory connectionFactory, Queue queue) {
        JmsTemplate jmsTemplate = new JmsTemplate();

        jmsTemplate.setDefaultDestination(queue);
        jmsTemplate.setConnectionFactory(connectionFactory);

        return jmsTemplate;
    }

    @Bean("destinationQueue")
    Queue getDestinationQueue() {
        return new ActiveMQQueue("IN_QUEUE");
    }

    @Bean("connectionFactory")
    ConnectionFactory getConnectionFactory(ConnectionFactory connectionFactory) {
        return new SingleConnectionFactory(connectionFactory);
    }

    @Bean("activeMqConnectionFactory")
    ConnectionFactory getActiveMQConnectionFactory() {
        return new ActiveMQConnectionFactory("vm://embedded");
    }

    @Bean("jmsContainer")
    DefaultMessageListenerContainer getDefaultMessageListenerContainer(ConnectionFactory connectionFactory,
                                                                       Queue queue,
                                                                       MessageListener messageListener) {
        DefaultMessageListenerContainer defaultMessageListenerContainer = new DefaultMessageListenerContainer();

        defaultMessageListenerContainer.setConnectionFactory(connectionFactory);
        defaultMessageListenerContainer.setDestination(queue);
        defaultMessageListenerContainer.setMessageListener(messageListener);

        return defaultMessageListenerContainer;
    }

    @Bean("broker")
    BrokerService getBrokerService() throws Exception {
        BrokerService brokerService = new BrokerService();
        brokerService.setUseJmx(false);
        brokerService.setPersistent(false);
        brokerService.setBrokerName("embedded");
        brokerService.setUseShutdownHook(false);
        brokerService.addConnector(environment.getProperty("activemq.broker-url"));
        brokerService.start();

        return brokerService;
    }

    @Bean("WorldApiClient")
    WebClient getWorldApiClient() {
        return WebClient.builder().baseUrl(worldWebClientConfiguration.getBaseUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultUriVariables(Collections.singletonMap("url", worldWebClientConfiguration.getBaseUrl()))
                .build();
    }

    @Bean
    public OpenAPI customOpenAPI(@Value("${api.version}") String appVersion) {
        return new OpenAPI()
                .components(new Components().addSecuritySchemes("basicScheme",
                        new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("basic")))
                .info(new Info().title("OpenBank API").version(appVersion).description(
                        "This is an local HTTPS OpenBank server. You can find out more about OpenBank at " +
                                "[http://jsquad.se](http://jsquad.se).")
                        .termsOfService("http://jsquad.se/terms/")
                        .license(new License().name("Apache 2.0").url("https://www.apache.org/licenses/LICENSE-2.0" +
                                ".html")))
                .addServersItem(new Server().description("Local OpenBank API server.").url("https://localhost:8443"));
    }
}
