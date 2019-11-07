FROM jboss/wildfly:16.0.0.Final

ENV WILDFLY_HOME /opt/jboss/wildfly

RUN $WILDFLY_HOME/bin/add-user.sh --silent admin admin1234
RUN $WILDFLY_HOME/bin/add-user.sh -a -g admin --silent root root
RUN $WILDFLY_HOME/bin/add-user.sh -a -g customer --silent john doe

COPY target/openbank-spring-1.0-SNAPSHOT.war $WILDFLY_HOME/standalone/deployments/openbank-spring-1.0-SNAPSHOT.war

EXPOSE 8080 9990

CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]
