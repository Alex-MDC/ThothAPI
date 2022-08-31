
################
FROM maven:3.8.6-jdk-8-slim
WORKDIR /home/demo/

COPY demo/pom.xml .
COPY demo/src/main/resources/application.properties .
COPY demo/src ./src

RUN mvn clean install -e

ENTRYPOINT ["java","-jar","./target/demo-0.0.1-SNAPSHOT.jar"]

