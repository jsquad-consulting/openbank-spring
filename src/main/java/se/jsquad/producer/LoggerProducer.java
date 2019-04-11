package se.jsquad.producer;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class LoggerProducer {
    @Bean("logger")
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    public Logger logger(final InjectionPoint injectionPoint) {
        return LogManager.getLogger(injectionPoint.getMethodParameter().getContainingClass());
    }

}
