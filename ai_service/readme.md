# AI Service

This microservice provides AI-powered recommendations and analysis for user activities. It integrates with external AI APIs and communicates with other services via messaging.

## Features
- Analyze activity data
- Generate recommendations and suggestions
- Integrate with Gemini or other AI APIs
- Receive and send messages via RabbitMQ

## Technologies Used
- Java 17+
- Spring Boot
- Spring WebFlux
- RabbitMQ
- Lombok
- Docker

## Endpoints
- `POST /api/v1/ai/analyze` - Analyze activity and get recommendations
- `GET /api/v1/ai/recommendations/{activityId}` - Get recommendations for activity

## How to Run
1. Ensure RabbitMQ is running (see docker-compose.yml)
2. Build and run the service:
   ```
   ./mvnw spring-boot:run
   ```
3. Service will be available at `http://localhost:<port>` (default: 8086)

## Configuration
- Managed via config server (`config_server/config/ai-service.yml`)

## Integration
- Communicates with activity service and other microservices
- Uses RabbitMQ for messaging

## Troubleshooting
- Check logs for errors related to messaging or AI API connectivity

## Contribution
1. Fork the repo and create a feature branch
2. Make changes and add tests
3. Submit a pull request

 

