FROM openjdk:21-jdk-slim
WORKDIR /app
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
RUN ./mvnw dependency:go-offline -B
COPY src src
RUN ./mvnw package -DskipTests
COPY target target
EXPOSE 8082
HEALTHCHECK --interval=10s --timeout=5s --retries=10 CMD curl -f http://discovery-service:8761/actuator/health || exit 1
ENTRYPOINT ["java","-jar","/app/target/ticket-service-0.0.1-SNAPSHOT.jar"]