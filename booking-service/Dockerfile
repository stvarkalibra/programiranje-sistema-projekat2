# Stage 1: Build faza (Koristimo Maven i JDK)
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# Instaliramo shared-module u lokalni Maven repo
COPY shared-module/pom.xml shared-module/pom.xml
COPY shared-module/src shared-module/src
RUN mvn -f shared-module/pom.xml install -DskipTests -B

# Kopiramo samo pom.xml da bi Docker keširao dependency-je
COPY booking-service/pom.xml booking-service/pom.xml
RUN mvn -f booking-service/pom.xml dependency:go-offline -B || true

# Kopiramo source kod
COPY booking-service/src booking-service/src

# Pakujemo aplikaciju bez testova
RUN mvn -f booking-service/pom.xml clean package -DskipTests -B

# Stage 2: Runtime faza (Minimalni JRE)
FROM eclipse-temurin:21-jre
WORKDIR /app

# Kopiramo samo gotov .jar iz prve faze
COPY --from=build /app/booking-service/target/*.jar app.jar

# Port servisa
ENV SERVER_PORT=8083

EXPOSE 8083

# Komanda za pokretanje
ENTRYPOINT ["java", "-jar", "app.jar"]
