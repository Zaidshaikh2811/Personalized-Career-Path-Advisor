# Gateway Service

This microservice acts as the API Gateway for the Child1 Microservices Project. It handles routing, load balancing, authentication filtering, and request forwarding to other microservices.

## Features
- Centralized API routing for all microservices
- JWT authentication and validation
- Load balancing using service discovery (Eureka)
- Request filtering and forwarding

## Technologies Used
- Java 17+
- Spring Boot
- Spring Cloud Gateway
- Eureka Client
- Docker

## How to Run
1. Ensure Eureka server and all target microservices are running
2. Build and run the gateway:
   ```
   ./mvnw spring-boot:run
   ```
3. Gateway will be available at `http://localhost:8085` (or configured port)

## Configuration
- Managed via config server (`config_server/config/gateway.yml`)
- Routing rules and filters are defined in `application.yml`

## Integration
- Forwards requests to activity, user, auth, and AI services
- Uses Eureka for service discovery

## Troubleshooting
- Check logs for errors related to routing or service discovery
- Ensure all required services are registered in Eureka

## Contribution
1. Fork the repo and create a feature branch
2. Make changes and add tests
3. Submit a pull request
 

