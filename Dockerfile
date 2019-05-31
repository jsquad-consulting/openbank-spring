FROM maven:3.6.0-jdk-11

ENV WILDFLY_VERSION 16.0.0.Final
ENV WILDFLY_HOME /usr

RUN rm -fr /usr/wildfly

RUN cd $WILDFLY_HOME && curl http://download.jboss.org/wildfly/$WILDFLY_VERSION/wildfly-$WILDFLY_VERSION.tar.gz \
| tar zx && mv $WILDFLY_HOME/wildfly-$WILDFLY_VERSION $WILDFLY_HOME/wildfly

RUN /usr/wildfly/bin/add-user.sh --silent admin admin1234
RUN /usr/wildfly/bin/add-user.sh -a -g admin --silent root root
RUN /usr/wildfly/bin/add-user.sh -a  -g customer --silent john doe

ADD . /usr/openbank-spring

RUN mvn -f /usr/openbank-spring/pom.xml clean install

RUN cp /usr/openbank-spring/target/openbank-spring-1.0-SNAPSHOT.war $WILDFLY_HOME/wildfly/standalone/deployments/.

CMD ["/usr/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]
