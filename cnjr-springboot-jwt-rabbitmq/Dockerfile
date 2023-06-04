FROM openjdk:17-jdk-slim-buster
WORKDIR /app

COPY target/rabbitmq-0.0.1-SNAPSHOT.jar build/

WORKDIR /app/build
ENTRYPOINT java -jar rabbitmq-0.0.1-SNAPSHOT.jar