package se.jsquad.interceptor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Order(value = 2)
public class LoggingAspect {
    @Around("execution(* se.jsquad.*.*(..))")
    public Object logMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        final Logger logger = LogManager.getLogger(joinPoint.getTarget().getClass().getName());
        Object returnValue;

        try {
            StringBuilder startMessageStringBuilder = new StringBuilder();

            startMessageStringBuilder.append(joinPoint.getSignature().getName());
            startMessageStringBuilder.append("(");

            Object[] args = joinPoint.getArgs();
            for (int i = 0; i < args.length; ++i) {
                startMessageStringBuilder.append(args[i]).append(",");
            }
            if (args.length > 0) {
                startMessageStringBuilder.deleteCharAt(startMessageStringBuilder.length() - 1);
            }

            startMessageStringBuilder.append(")");

            logger.trace(startMessageStringBuilder.toString());

            returnValue = joinPoint.proceed();

            StringBuilder endMessageStringBuilder = new StringBuilder();
            endMessageStringBuilder.append("Finish method ");
            endMessageStringBuilder.append(joinPoint.getSignature().getName());

            logger.trace(endMessageStringBuilder.toString());
        } catch (Throwable e) {
            StringBuilder errorMessageStringBuilder = new StringBuilder();
            logger.error(errorMessageStringBuilder.toString(), e);

            throw e;
        }

        return returnValue;
    }
}
