package se.jsquad.configuration;


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
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
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
import org.springframework.ws.config.annotation.EnableWs;

import javax.jms.ConnectionFactory;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import javax.validation.Validator;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@EnableScheduling
@EnableWs
@EnableAsync
@EnableJms
@ComponentScan(basePackages = {"se.jsquad"})
@EnableJpaRepositories(basePackages = {"se.jsquad.repository"})
@PropertySource("classpath:openbank_database.properties")
@PropertySource("classpath:security_database.properties")
@PropertySource("classpath:activemq.properties")
public class ApplicationConfiguration {
    private Environment environment;

    public ApplicationConfiguration(Environment environment) {
        this.environment = environment;
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

    @Bean("dataSourceOpenBank")
    DataSource dataSourceOpenBank() {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName(environment.getProperty("openbank.datasource.driverClassName"));
        dataSourceBuilder.url(environment.getProperty("openbank.datasource.url"));

        return dataSourceBuilder.build();
    }

    @Bean("dataSourceSecurity")
    DataSource dataSourceSecurity() {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName(environment.getProperty("security.datasource.driverClassName"));
        dataSourceBuilder.url(environment.getProperty("security.datasource.url"));

        return dataSourceBuilder.build();
    }

    @Bean("entityManagerFactoryOpenBank")
    LocalContainerEntityManagerFactoryBean getLocalContainerEntityManagerFactoryBeanOpenBank(
            JpaVendorAdapter jpaVendorAdapter, @Qualifier("dataSourceOpenBank") DataSource dataSource,
            @Value("#{dbProds.openbank_pu}") String persistenceUnitName) {
        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setJpaVendorAdapter(jpaVendorAdapter);
        factoryBean.setPersistenceUnitName(persistenceUnitName);
        factoryBean.setDataSource(dataSource);

        Properties properties = new Properties();

        properties.setProperty("hibernate.dialect", environment.getProperty("openbank.jpa.database-platform"));

        properties.setProperty("javax.persistence.schema-generation.database.action", environment
                .getProperty("openbank.javax.persistence.schema-generation.database.action"));

        properties.setProperty("hibernate.cache.use_second_level_cache", environment
                .getProperty("openbank.jpa.hibernate.cache.use_second_level_cache"));

        properties.setProperty("hibernate.cache.region.factory_class", environment
                .getProperty("openbank.jpa.hibernate.cache.region.factory.class"));

        factoryBean.setJpaProperties(properties);

        return factoryBean;
    }

    @Bean("entityManagerFactorySecurity")
    LocalContainerEntityManagerFactoryBean getLocalContainerEntityManagerFactoryBeanSecurity(
            JpaVendorAdapter jpaVendorAdapter, @Qualifier("dataSourceSecurity") DataSource dataSource,
            @Value("#{dbProds.security_pu}") String persistenceUnitName) {
        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setJpaVendorAdapter(jpaVendorAdapter);
        factoryBean.setPersistenceUnitName(persistenceUnitName);
        factoryBean.setDataSource(dataSource);

        Properties properties = new Properties();

        properties.setProperty("hibernate.dialect", environment.getProperty("security.jpa.database-platform"));

        properties.setProperty("javax.persistence.schema-generation.database.action", environment
                .getProperty("security.javax.persistence.schema-generation.database.action"));

        properties.setProperty("hibernate.cache.use_second_level_cache", environment
                .getProperty("security.jpa.hibernate.cache.use_second_level_cache"));

        properties.setProperty("hibernate.cache.region.factory_class", environment
                .getProperty("security.jpa.hibernate.cache.region.factory.class"));

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

    private JpaTransactionManager getJpaTransactionManager(@Qualifier("entityManagerFactorySecurity") EntityManagerFactory entityManagerFactory) {
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

    private TransactionTemplate getTransactionTemplate(@Qualifier("transactionManagerSecurity") JpaTransactionManager jpaTransactionManager) {
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

    @Bean
    public OpenAPI customOpenAPI(@Value("${api.version}") String appVersion) {
        return new OpenAPI()
                .components(new Components().addSecuritySchemes("basicScheme",
                        new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("basic")))
                .info(new Info().title("OpenBank API").version(appVersion).description(
                        "This is an OpenBank server. You can find out more about OpenBank at " +
                                "[http://jsquad.se](http://jsquad.se).")
                        .termsOfService("http://jsquad.se/terms/")
                        .license(new License().name("Apache 2.0").url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .addServersItem(new Server().description("Local OpenBank API server.").url("http://localhost:8080"));
    }
}
