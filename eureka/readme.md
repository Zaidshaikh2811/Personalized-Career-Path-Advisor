# Eureka Server

This microservice provides service discovery for the Child1 Microservices Project. All other microservices register themselves with Eureka, enabling dynamic routing and load balancing.

## Features
- Service registry and discovery
- Health checks for registered services
- Dashboard for monitoring service status

## Technologies Used
- Java 17+
- Spring Boot
- Spring Cloud Netflix Eureka
- Docker

## How to Run
1. Build and run the Eureka server:
   ```
   ./mvnw spring-boot:run
   ```
2. Eureka dashboard will be available at `http://localhost:8761`

## Configuration
- Managed via config server (`config_server/config/eureka.yml`)
- Service registration settings in `application.yml`

## Integration
- All microservices register with Eureka for service discovery
- API Gateway uses Eureka for load balancing

## Troubleshooting
- Check logs for errors related to service registration
- Ensure all microservices are configured to register with Eureka

## Contribution
1. Fork the repo and create a feature branch
2. Make changes and add tests
3. Submit a pull request



