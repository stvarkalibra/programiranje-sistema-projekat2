FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY shared-module/pom.xml shared-module/pom.xml
COPY shared-module/src shared-module/src
RUN mvn -f shared-module/pom.xml install -DskipTests -B

COPY avantura-service/pom.xml avantura-service/pom.xml
RUN mvn -f avantura-service/pom.xml dependency:go-offline -B || true

COPY avantura-service/src avantura-service/src

RUN mvn -f avantura-service/pom.xml clean package -DskipTests -B

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/avantura-service/target/*.jar app.jar

ENV SERVER_PORT=8088

EXPOSE 8088

ENTRYPOINT ["java", "-jar", "app.jar"]