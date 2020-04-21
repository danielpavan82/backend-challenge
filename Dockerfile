FROM openjdk:11.0.3-jre

RUN mkdir /opt/backend-challenge

WORKDIR /opt/backend-challenge

COPY ./target/backend-challenge*.jar backend-challenge.jar

SHELL ["/bin/bash","-c"]

EXPOSE 8080
EXPOSE 5005

ENTRYPOINT java ${ADDITIONAL_OPTS} -jar backend-challenge.jar
