FROM openjdk:8-jdk-alpine
ADD target/phones-app-v1.1.jar phones-app-v1.1.jar
COPY src/main/resources/sample.db sample.db
ENTRYPOINT ["java","-jar","phones-app-v1.1.jar"]