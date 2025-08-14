#  Microservices Project

This repository contains a microservices-based system for activity tracking, AI recommendations, authentication, user management, and API gateway routing. The project is built using Spring Boot, Spring Cloud, Docker, and other modern Java technologies.

## Project Structure

- **activity_service/**: Manages user activities (CRUD operations, metrics, etc.).
- **ai_service/**: Provides AI-powered recommendations and analysis for activities.
- **auth_service/**: Handles user authentication, registration, and JWT token management.
- **user_service/**: Manages user profiles and related data.
- **gateway/**: API Gateway for routing, load balancing, and authentication filtering.
- **config_server/**: Centralized configuration management for all services.
- **eureka/**: Service discovery using Netflix Eureka.
- **docker-compose.yml**: Container orchestration for local development.

## Technologies Used

- Java 17+
- Spring Boot
- Spring Cloud (Gateway, Config, Eureka)
- Docker & Docker Compose
- PostgreSQL (for persistence)
- RabbitMQ (for messaging)
- JWT (for authentication)
- Lombok (for DTOs and models)
- Maven (build tool)

## How to Run

1. **Clone the repository**
   ```
   git clone <repo-url>
   cd java_proect
   ```

2. **Start services with Docker Compose**
   ```
   docker-compose up -d
   ```

3. **Build and run each microservice**
   ```
   cd <service-folder>
   ./mvnw spring-boot:run
   ```

4. **Access Eureka dashboard**
   - Visit `http://localhost:8761` to see registered services.

5. **API Gateway**
   - All APIs are accessible via the gateway at `http://localhost:8085` (or configured port).

## Configuration

- Centralized configs are in `config_server/config/`.
- Each service has its own `application.yml` for local overrides.
- Sensitive keys (e.g., JWT secret, DB passwords) should be managed via environment variables or config server.

## Endpoints Overview

- **Activity Service**: `/api/v1/activities`
- **AI Service**: `/api/v1/ai`
- **Auth Service**: `/api/v1/auth`
- **User Service**: `/api/v1/users`
- **Gateway**: Routes all requests and validates JWT tokens.

## Development Notes

- All routes except registration/login are protected by JWT.
- Services communicate via REST and RabbitMQ.
- Use Eureka for service discovery and Gateway for routing.
- For local development, ensure Docker is running and ports are not conflicting.

## Troubleshooting

- If a service fails to start, check logs for missing configs, port conflicts, or missing dependencies.
- Ensure all required Docker containers (DB, RabbitMQ) are running.
- For database errors, verify credentials and network settings in `docker-compose.yml` and `application.yml`.

## Contribution

1. Fork the repo and create a feature branch.
2. Make changes and add tests.
3. Submit a pull request with a clear description.

# System Design & Working Overview

## Architecture

This project is a distributed microservices system for activity tracking, user management, authentication, and AI-powered recommendations. It uses Spring Boot, Spring Cloud, Docker, and other modern Java technologies. The system is designed for scalability, modularity, and security.

### Microservices
- **activity_service**: Handles CRUD operations for user activities, stores metrics, and sends activity data to RabbitMQ for AI analysis.
- **ai_service**: Listens to activity messages, analyzes data using external AI APIs (e.g., Gemini), and returns recommendations.
- **auth_service**: Manages user registration, login, and JWT token generation/validation. Integrates with user_service for profile management.
- **user_service**: Manages user profiles and related data. Exposes REST APIs for user CRUD operations.
- **gateway**: API Gateway for routing, load balancing, and authentication filtering. All requests pass through the gateway, which validates JWT tokens and forwards to the correct service.
- **config_server**: Centralized configuration management for all services. Configs are stored in YAML files or Git and served to clients at startup.
- **eureka**: Service discovery. All services register with Eureka, enabling dynamic routing and load balancing.

### Communication
- **REST APIs**: Services communicate via HTTP endpoints exposed through the gateway.
- **RabbitMQ**: Used for asynchronous messaging between activity_service and ai_service.
- **Service Discovery**: Eureka enables dynamic lookup of service instances for routing and load balancing.
- **Config Server**: All services fetch configuration from config_server at startup.

## API Flow Example
1. **User Registration/Login**
   - User sends registration/login request to gateway (`/api/v1/auth/register` or `/api/v1/auth/login`).
   - Gateway forwards to auth_service, which creates user or validates credentials, then returns a JWT token.
2. **Protected API Call**
   - User sends request (e.g., create activity) with JWT token in header to gateway (`/api/v1/activities`).
   - Gateway validates token via auth_service before forwarding to activity_service.
   - activity_service processes request, stores activity, and sends message to RabbitMQ.
   - ai_service receives message, analyzes activity, and stores/sends recommendations.
3. **User Profile Management**
   - Requests to `/api/v1/users` are routed to user_service, which manages user data.

## Security
- **JWT Authentication**: All routes except registration/login are protected. Gateway validates tokens before forwarding requests.
- **Password Encryption**: Passwords are hashed before storage.

## Configuration & Deployment
- **Config Server**: All configs (DB, RabbitMQ, API keys, etc.) are managed centrally and can be updated without redeploying services.
- **Docker Compose**: Used to orchestrate all services, databases, and RabbitMQ for local development.
- **Eureka**: Ensures all services are discoverable and enables load balancing.

## Error Handling & Troubleshooting
- Gateway returns 401 for invalid tokens, 404 for unknown routes, and 500 for internal errors.
- Each service logs errors for easier debugging (e.g., DB connection issues, missing configs).
- Config server and Eureka dashboards provide visibility into system health and configuration.

## Extensibility
- New microservices can be added easily by registering with Eureka and updating gateway routes.
- Configs can be versioned and managed via Git for CI/CD.
- AI service can be extended to support more models or external APIs.

## Example API Endpoints
- `POST /api/v1/auth/register` - Register user
- `POST /api/v1/auth/login` - Login and get JWT
- `GET /api/v1/auth/validate` - Validate JWT token
- `POST /api/v1/activities` - Create activity
- `GET /api/v1/activities/{id}` - Get activity
- `POST /api/v1/ai/analyze` - Analyze activity
- `GET /api/v1/users/{id}` - Get user profile

## Monitoring & Scaling
- Use Docker Compose for local orchestration; scale services by increasing container count.
- Eureka and Gateway support horizontal scaling and load balancing.

## Summary
This system is a robust, scalable, and secure microservices architecture for activity tracking and AI recommendations, with centralized configuration, service discovery, and API gateway routing. All services are loosely coupled and can be developed, deployed, and scaled independently.

For more details, see individual service README files or documentation in each folder.

## System Architecture Diagram

Below is a conceptual diagram of the microservices architecture. You can visualize or generate this using tools like draw.io, Lucidchart, or Mermaid:

```
+-------------------+        +-------------------+        +-------------------+
|                   |        |                   |        |                   |
|  User/Client      +------->|   API Gateway     +------->|   Microservices   |
|                   |        |                   |        |                   |
+-------------------+        +-------------------+        +-------------------+
                                                        /      |      |      \
                                                       /       |      |       \
                                                      /        |      |        \
                                        +----------------+ +----------------+ +----------------+ +----------------+
                                        | activity_svc   | | ai_svc        | | user_svc      | | auth_svc      |
                                        +----------------+ +----------------+ +----------------+ +----------------+
                                                |                  |                 |                |
                                                |                  |                 |                |
                                                v                  v                 v                v
                                         +-------------------------------------------------------------+
                                         |                    RabbitMQ (Message Queue)                 |
                                         +-------------------------------------------------------------+

+-------------------+
|                   |
|   Config Server   |
|                   |
+-------------------+
        ^
        |
+-------------------+
|                   |
|     Eureka        |
|   (Service Disc.) |
|                   |
+-------------------+
```

- All microservices register with Eureka for service discovery.
- Config Server provides configuration to all services.
- API Gateway routes requests, validates JWT, and forwards to services.
- activity_service and ai_service communicate asynchronously via RabbitMQ.

 
