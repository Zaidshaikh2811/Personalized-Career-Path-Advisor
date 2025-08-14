# Activity Service

This microservice manages user activities, including creation, retrieval, updating, and deletion of activity records. It also handles metrics and integrates with AI recommendations.

## Features
- CRUD operations for activities
- Validation and checks for activity data
- Integration with AI service for recommendations
- Messaging via RabbitMQ

## Technologies Used
- Java 17+
- Spring Boot
- Spring Data JPA
- RabbitMQ
- PostgreSQL
- Lombok
- Docker

## Endpoints
- `POST /api/v1/activities` - Create activity
- `GET /api/v1/activities/{id}` - Get activity by ID
- `PUT /api/v1/activities/{id}` - Update activity
- `DELETE /api/v1/activities/{id}` - Delete activity
- `GET /api/v1/activities` - List all activities

## How to Run
1. Ensure PostgreSQL and RabbitMQ are running (see docker-compose.yml)
2. Build and run the service:
   ```
   ./mvnw spring-boot:run
   ```
3. Service will be available at `http://localhost:<port>` (default: 8082)

## Configuration
- Managed via config server (`config_server/config/activity-service.yml`)

## Integration
- Communicates with AI service and other microservices
- Uses RabbitMQ for messaging

## Troubleshooting
- Check logs for errors related to messaging or database

## Contribution
1. Fork the repo and create a feature branch
2. Make changes and add tests
3. Submit a pull request

 
