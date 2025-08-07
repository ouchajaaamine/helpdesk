# Gateway Service

This is a Spring Cloud Gateway service that acts as an API gateway for the helpdesk application.

## Features

- **Service Discovery**: Automatically discovers services registered with Eureka
- **Load Balancing**: Built-in load balancing for multiple service instances
- **Routing**: Intelligent routing based on service names
- **Monitoring**: Actuator endpoints for health checks and gateway metrics

## Configuration

The gateway is configured to:
- Run on port 8080
- Connect to Eureka discovery service on localhost:8761
- Automatically create routes for discovered services
- Enable debug logging for troubleshooting

## Running the Service

### Prerequisites
- Java 21 or higher
- Discovery service running on port 8761

### Run with Maven
```bash
./mvnw spring-boot:run
```

### Build and Run
```bash
./mvnw clean package
java -jar target/gateway-service-0.0.1-SNAPSHOT.jar
```

## API Endpoints

- **Health Check**: `GET /actuator/health`
- **Gateway Routes**: `GET /actuator/gateway/routes`
- **Service Routes**: All registered services are accessible via `/{service-name}/**`

## Usage

Once the gateway is running, you can access any registered service through the gateway:
- `http://localhost:8080/{service-name}/{endpoint}`

The gateway will automatically discover services from Eureka and create routes for them.
