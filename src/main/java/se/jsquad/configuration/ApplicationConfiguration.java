package se.jsquad.configuration;


import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
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
import javax.validation.Validator;

@Configuration
@EnableTransactionManagement
@EnableScheduling
@EnableWs
@EnableAsync
@EnableJms
@ComponentScan(basePackages = {"se.jsquad"})
@EnableJpaRepositories(basePackages = {"se.jsquad.repository"})
public class ApplicationConfiguration {
    @Bean("logger")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    Logger getLogger(final InjectionPoint injectionPoint) {
        return LogManager.getLogger(injectionPoint.getMember().getDeclaringClass().getName());
    }

    @Bean("validator")
    Validator validator() {
        return new LocalValidatorFactoryBean();
    }

    @Bean("entityManagerFactory")
    @Autowired
    LocalContainerEntityManagerFactoryBean getLocalContainerEntityManagerFactoryBean(
            @Qualifier("hibernateVendorAdapter") JpaVendorAdapter jpaVendorAdapter,
            @Value("#{dbProds.pu}") String persistenceUnitName) {
        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setJpaVendorAdapter(jpaVendorAdapter);
        factoryBean.setPersistenceUnitName(persistenceUnitName);

        return factoryBean;
    }

    @Bean("hibernateVendorAdapter")
    JpaVendorAdapter getJpaVendorAdapter() {
        return new HibernateJpaVendorAdapter();
    }

    @Bean("transactionManager")
    @Autowired
    JpaTransactionManager getJpaTransactionManager(@Qualifier("entityManagerFactory")
                                                           EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setEntityManagerFactory(entityManagerFactory);

        return jpaTransactionManager;
    }

    @Bean("transactionTemplate")
    TransactionTemplate getTransactionTemplate(@Qualifier("transactionManager") JpaTransactionManager
                                                       jpaTransactionManager) {
        TransactionTemplate transactionTemplate = new TransactionTemplate();
        transactionTemplate.setTransactionManager(jpaTransactionManager);
        transactionTemplate.setPropagationBehavior(Propagation.REQUIRED.value());

        return transactionTemplate;
    }

    @Bean("jmsTemplate")
    JmsTemplate getJmsTemplate(@Qualifier("connectionFactory") ConnectionFactory connectionFactory,
                               @Qualifier("destinationQueue") Queue queue) {
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
    ConnectionFactory getConnectionFactory(@Qualifier("activeMqConnectionFactory") ConnectionFactory connectionFactory) {
        return new SingleConnectionFactory(connectionFactory);
    }

    @Bean("activeMqConnectionFactory")
    ConnectionFactory getActiveMQConnectionFactory() {
        return new ActiveMQConnectionFactory("tcp://localhost:61616");
    }

    @Bean("jmsContainer")
    DefaultMessageListenerContainer getDefaultMessageListenerContainer(@Qualifier("connectionFactory")
                                                                               ConnectionFactory connectionFactory,
                                                                       @Qualifier("destinationQueue") Queue queue,
                                                                       @Qualifier("jmsMessageListener")
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
        brokerService.addConnector("tcp://localhost:61616");
        brokerService.start();

        return brokerService;
    }
}
