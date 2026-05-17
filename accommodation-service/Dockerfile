# Build faza
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# 1. Instaliraj shared-module u lokalni Maven repo
COPY shared-module/pom.xml ./shared-module/pom.xml
COPY shared-module/src ./shared-module/src
RUN mvn install -f shared-module/pom.xml -DskipTests

# 2. Build accommodation-service
COPY accommodation-service/pom.xml ./accommodation-service/pom.xml
RUN mvn dependency:go-offline -f accommodation-service/pom.xml -B
COPY accommodation-service/src ./accommodation-service/src
RUN mvn clean package -f accommodation-service/pom.xml -DskipTests

# Runtime faza
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/accommodation-service/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
