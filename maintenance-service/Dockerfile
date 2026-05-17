# Build faza
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# 1. Instaliraj shared-module u lokalni Maven repo
COPY shared-module/pom.xml ./shared-module/pom.xml
COPY shared-module/src ./shared-module/src
RUN mvn install -f shared-module/pom.xml -DskipTests

# 2. Build maintenance-service
COPY maintenance-service/pom.xml ./maintenance-service/pom.xml
RUN mvn dependency:go-offline -f maintenance-service/pom.xml -B
COPY maintenance-service/src ./maintenance-service/src
RUN mvn clean package -f maintenance-service/pom.xml -DskipTests

# Runtime faza
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/maintenance-service/target/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
