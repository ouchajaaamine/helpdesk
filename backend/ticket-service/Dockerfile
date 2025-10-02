FROM openjdk:21-jdk-slim

# Install curl for healthcheck
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

RUN ./mvnw dependency:go-offline -B

COPY src src
RUN ./mvnw package -DskipTests

EXPOSE 8082

# Fix: Healthcheck should check THIS service, not discovery-service
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8082/actuator/health || exit 1

# Simple entrypoint - no wait script needed for now
ENTRYPOINT ["java", "-jar", "target/ticket-service-0.0.1-SNAPSHOT.jar"]