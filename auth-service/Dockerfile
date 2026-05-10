# Build faza
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime faza
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
# postojeca baza ako postoji
COPY auth_database.db auth_database.db
EXPOSE 8085
ENTRYPOINT ["java", "-jar", "app.jar"]