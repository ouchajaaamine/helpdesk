# Use a base image with Java 21
FROM openjdk:21-jdk-slim

# Install curl for healthcheck
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Set the working directory inside the container
WORKDIR /app

# Copy the Maven wrapper and pom.xml to leverage Docker's layer caching
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# FIX: Convert line endings from Windows (CRLF) to Unix (LF) AND add execute permission
RUN sed -i 's/\r$//' ./mvnw && chmod +x ./mvnw

# Download all dependencies. This step is cached by Docker.
RUN ./mvnw dependency:go-offline -B

# Copy the rest of the application source code
COPY src src

# Build the application
RUN ./mvnw package -DskipTests

# Expose the port the application runs on
EXPOSE 8761

# Add health check for better container management
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8761/actuator/health || exit 1

# Define the command to run the application
ENTRYPOINT ["java", "-jar", "target/discovery-service-0.0.1-SNAPSHOT.jar"]