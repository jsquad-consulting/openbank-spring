FROM openjdk:11-jre-slim

COPY target/openbank-spring-0.0.1-SNAPSHOT.jar /app/openbank-spring-0.0.1-SNAPSHOT.jar

EXPOSE 8080 9990

CMD ["java", "-jar", "/app/openbank-spring-0.0.1-SNAPSHOT.jar"]