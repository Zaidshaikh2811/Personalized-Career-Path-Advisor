# Config Server

This microservice provides centralized configuration management for all services in the Child1 Microservices Project. It allows dynamic and versioned configuration updates for microservices using Spring Cloud Config.

## Features
- Centralized configuration for all microservices
- Supports YAML, properties, and environment variables
- Dynamic config refresh for clients
- Integration with Git or local file system for config storage

## Technologies Used
- Java 17+
- Spring Boot
- Spring Cloud Config Server
- Docker

## How to Run
1. Build and run the config server:
   ```
   ./mvnw spring-boot:run
   ```
2. Config server will be available at `http://localhost:8888`

## Configuration
- Config files are stored in `config_server/config/` or a connected Git repository
- Each microservice reads its config from the config server at startup

## Integration
- All microservices (activity, ai, auth, user, gateway, eureka) fetch their configs from this server
- Supports config refresh endpoints for dynamic updates

## Troubleshooting
- Check logs for errors related to config loading or file access
- Ensure config files are present and correctly formatted

## Contribution
1. Fork the repo and create a feature branch
2. Make changes and add tests
3. Submit a pull request

 

