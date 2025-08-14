# Auth Service

This microservice handles user authentication, registration, and JWT token management for the Child1 Microservices Project.

## Features
- User registration and login
- JWT token generation and validation
- Password encryption
- Integration with user service and API gateway

## Technologies Used
- Java 17+
- Spring Boot
- Spring Security
- JWT (io.jsonwebtoken)
- PostgreSQL
- Lombok
- Docker

## Endpoints
- `POST /api/v1/auth/register` - Register a new user
- `POST /api/v1/auth/login` - Login and get JWT token
- `GET /api/v1/auth/validate` - Validate JWT token

## How to Run
1. Ensure PostgreSQL is running (see docker-compose.yml)
2. Build and run the service:
   ```
   ./mvnw spring-boot:run
   ```
3. Service will be available at `http://localhost:<port>` (default: 8084)

## Configuration
- Managed via config server (`config_server/config/auth-service.yml`)
- Sensitive data (JWT secret, DB password) should be managed via environment variables or config server

## Integration
- Works with API Gateway for authentication filtering
- Communicates with user service

## Troubleshooting
- Check logs for errors related to authentication or database
- Ensure all required containers are running

## Contribution
1. Fork the repo and create a feature branch
2. Make changes and add tests
3. Submit a pull request

 

