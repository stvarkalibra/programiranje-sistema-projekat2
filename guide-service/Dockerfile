FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY shared-module/pom.xml shared-module/pom.xml
COPY shared-module/src shared-module/src
RUN mvn -f shared-module/pom.xml install -DskipTests -B

COPY guide-service/pom.xml guide-service/pom.xml
RUN mvn -f guide-service/pom.xml dependency:go-offline -B || true

COPY guide-service/src guide-service/src

RUN mvn -f guide-service/pom.xml clean package -DskipTests -B

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/guide-service/target/*.jar app.jar

ENV SERVER_PORT=8087

EXPOSE 8087

ENTRYPOINT ["java", "-jar", "app.jar"]