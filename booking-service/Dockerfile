# Stage 1: Build faza (Koristimo Maven i JDK)
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# Kopiramo samo pom.xml da bi Docker keširao dependency-je
COPY pom.xml .

# Skidamo Maven dependency-je i plugin-e
# Ne pokrećemo git-commit-id plugin jer u Docker buildu nema .git foldera
RUN mvn dependency:go-offline -B && \
    mvn dependency:resolve-plugins -B || true

# Kopiramo source kod
COPY src ./src

# Pakujemo aplikaciju bez testova
RUN mvn clean package -DskipTests

# Stage 2: Runtime faza (Minimalni JRE)
FROM eclipse-temurin:21-jre
WORKDIR /app

# Kopiramo samo gotov .jar iz prve faze
COPY --from=build /app/target/*.jar app.jar

# Port servisa
ENV SERVER_PORT=8083

EXPOSE 8083

# Komanda za pokretanje
ENTRYPOINT ["java", "-jar", "app.jar"]