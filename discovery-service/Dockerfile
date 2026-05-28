FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY discovery-service/pom.xml .
RUN mvn dependency:go-offline -B

COPY discovery-service/src ./src
RUN mvn clean package spring-boot:repackage -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/target/discovery-service.jar app.jar

ENV SERVER_PORT=8761

EXPOSE 8761

ENTRYPOINT ["java", "-jar", "app.jar"]