FROM openjdk:8-jdk-alpine

COPY ./maven/demo-app.jar demo-app.jar
COPY ./wait-for-it.sh wait-for-it.sh

COPY ./wait-for.sh wait-for.sh

RUN chmod +x ./wait-for-it.sh
RUN chmod +x ./wait-for.sh


# ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/demo-app.jar"]
