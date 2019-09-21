# OpenBank Spring Boot application

## Requirements

* JDK 11
* Maven 3.6.0
* Docker 1.19+

## INSTALLATION
---------------

### Without Docker

````bash
mvn clean install -T 1C
````

### With Docker

````bash
# With env dependicies
mvn clean install -T 1C && docker build -t openbank .
# OR
docker build -f PipelineDockerfile -t openbank .
# OR With Compose and no env dependicies
docker-compose -f docker-compose-pipeline.yaml build
# OR With Compose and with env dependicies
mvn clean install -T 1C && docker-compose -f docker-compose.yaml build
````

## Start the application

### Without Docker

```bash
mvn spring-boot:run
# OR
java -jar target/openbank-spring-0.0.1-SNAPSHOT.jar
```

### With Docker

````bash
# With Docker
docker run --rm -p 8080:8080 -p 9990:9990 -it --name openbank_container openbank
# With Docker Compose (build included) with env dependicies
docker-compose -f docker-compose.yaml up --build --force-recreate
# With Docker Compose (build included) with no env dependicies
docker-compose -f docker-compose-pipeline.yaml up --build --force-recreate
````

### Clean up images and containers

`docker rm -vf $(docker ps -a -q)`

`docker rmi -f $(docker images -a -q)`

`docker system prune -af`

START THE DOCKER CONTAINER TO TEST RESTFUL/SOAP CONTRACTS
---------------------------------------------------------


TEST THE RESTfulCONTRACTS
-------------------------
Load the src/main/resources/schema/rest.yaml file with the 
http://editor.swagger.io/ editor to easily test the RESTful 
contracts. Be sure to use basic authorization with --user <user:pass>
flag to use the RESTful operations successfully.

