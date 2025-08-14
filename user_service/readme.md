# User Service

This microservice is responsible for managing user profiles and related data in the Child1 Microservices Project.

## Features
- Create, read, update, and delete user profiles
- Validate user data
- Integrate with authentication and activity services
- Expose RESTful APIs for user management

## Technologies Used
- Java 17+
- Spring Boot
- Spring Data JPA
- PostgreSQL
- Lombok
- Docker (for containerization)

## Endpoints
- `POST /api/v1/users` - Create a new user
- `GET /api/v1/users/{id}` - Get user by ID
- `PUT /api/v1/users/{id}` - Update user
- `DELETE /api/v1/users/{id}` - Delete user
- `GET /api/v1/users` - List all users

## How to Run
1. Ensure PostgreSQL is running (see docker-compose.yml)
2. Build and run the service:
   ```
   ./mvnw spring-boot:run
   ```
3. Service will be available at `http://localhost:<port>` (default: 8083)

## Configuration
- Database and other configs are managed via config server (`config_server/config/user-service.yml`)
- Sensitive data should be managed via environment variables or config server

## Integration
- Works with API Gateway for routing and authentication
- Communicates with other services via REST and RabbitMQ

## Troubleshooting
- Check logs for errors related to database connectivity or missing configs
- Ensure all required containers are running

## Contribution
1. Fork the repo and create a feature branch
2. Make changes and add tests
3. Submit a pull request

 

