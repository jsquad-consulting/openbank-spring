FROM openjdk:11-jre-slim

COPY target/openbank-spring-0.0.1-SNAPSHOT.jar /app/openbank-spring.jar

EXPOSE 8443 8081 8000

CMD ["sh", "-c", "java -jar /app/openbank-spring.jar --jasypt.encryptor.password=${MASTER_SECRET} \
--spring.config.location=${CONFIG_FILE_LOCATIONS}"]