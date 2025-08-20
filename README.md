<img width="1621" height="1015" alt="System_Design" src="https://github.com/user-attachments/assets/a2c0a68c-720b-4a78-b9c6-a2fef5f3fe5d" /># FitNest Fitness Platform

A scalable microservices-based fitness tracking and AI recommendation platform built with Java (Spring Boot), React, RabbitMQ, Docker, and more.

---

## Table of Contents
- [System Overview](#system-overview)
- [Architecture Diagram](#architecture-diagram)
- [Microservices & Responsibilities](#microservices--responsibilities)
- [API Endpoints](#api-endpoints)
- [Data Flow & Working](#data-flow--working)
- [Setup & Running Locally](#setup--running-locally)
- [Docker & Deployment](#docker--deployment)
- [Environment Variables](#environment-variables)
- [Development & Testing](#development--testing)
- [Troubleshooting](#troubleshooting)
- [Contributing](#contributing)
- [License](#license)

---

## System Overview
This platform allows users to:
- Track fitness activities (CRUD)
- Get AI-powered recommendations for activities
- Manage authentication and user profiles
- View dashboards and insights

**Tech Stack:**
- Java (Spring Boot) for backend microservices
- React for frontend
- RabbitMQ for messaging
- Eureka for service discovery
- Spring Cloud Config for centralized configuration
- Docker for containerization

---

## Architecture Diagram

<img width="1621" height="1015" alt="System_Design" src="https://github.com/user-attachments/assets/7a5305fc-6d39-4c6e-b919-9686fc266fa3" />

---
## Sequence Diagram
<img width="1317" height="3305" alt="sequence_Diagram" src="https://github.com/user-attachments/assets/0c06ac87-0833-4197-ab1d-b091419ea633" />

---

## Microservices & Responsibilities
- **activity_service/**: Manages user activities (CRUD operations, metrics, etc.).
- **ai_service/**: Provides AI-powered recommendations and analysis for activities.
- **auth_service/**: Handles user authentication, registration, and JWT token management.
- **user_service/**: Manages user profiles and related data.
- **gateway/**: API Gateway for routing, load balancing, and authentication filtering.
- **common-security/**: Shared security logic (JWT, filters).
- **config_server/**: Centralized configuration management for all services.
- **eureka/**: Service discovery using Netflix Eureka.
- **frontend/**: React-based user interface.
- **docker-compose.yml**: Container orchestration for local development.

---

## API Endpoints

### Activity Service
- `POST /api/v1/activities/create` - Create activity
- `GET /api/v1/activities` - List activities (paginated)
- `GET /api/v1/activities/{id}` - Get activity by ID
- `PUT /api/v1/activities/update/{id}` - Update activity
- `DELETE /api/v1/activities/delete/{id}` - Delete activity
- `GET /api/v1/activities/my-activities` - List user’s activities
- `GET /api/v1/activities/recent` - Recent activities
- `GET /api/v1/activities/top-calories` - Top calorie activities

### AI Service
- `GET /api/v1/recommendations` - List recommendations (paginated)
- `GET /api/v1/recommendations/{id}` - Get recommendation by ID
- `POST /api/v1/recommendations` - Create recommendation
- `PUT /api/v1/recommendations/{id}` - Update recommendation
- `DELETE /api/v1/recommendations/{id}` - Delete recommendation

### Auth Service
- `POST /api/v1/auth/register` - Register user
- `POST /api/v1/auth/login` - Login

### User Service
- `GET /api/v1/users/{id}` - Get user profile
- `PUT /api/v1/users/{id}` - Update user profile

### Gateway
- Routes all requests to appropriate microservices

---

## Data Flow & Working
1. **User creates/updates/deletes an activity via frontend.**
2. **Activity Service** processes the request and sends an event to RabbitMQ.
3. **AI Service** listens to RabbitMQ, processes the activity, and generates recommendations using AI (Gemini API or similar).
4. Recommendations are stored and can be fetched by the frontend.
5. All services register with **Eureka** for service discovery.
6. **Gateway** routes requests and applies security via **common-security**.

---

## Setup & Running Locally

### Prerequisites
- Java 17+
- Node.js 18+
- Docker & Docker Compose

### 1. Clone the repository
```sh
git clone <repo-url>
cd java_proect
```

### 2. Start RabbitMQ, Eureka, Config Server, and all services
#### Using Docker Compose
```sh
docker-compose up --build
```
#### Or manually
```sh
./start-all.sh
```

### 3. Start the frontend
```sh
cd frontend
npm install
npm run dev
```

### 4. Access the app
- Frontend: [http://localhost:3000](http://localhost:3000)
- Eureka dashboard: [http://localhost:8761](http://localhost:8761)
- RabbitMQ dashboard: [http://localhost:15672](http://localhost:15672) (default user/pass: guest/guest)

---

## Docker & Deployment
- All services are containerized.
- Use `docker-compose.yml` to orchestrate services.
- For production, configure environment variables/secrets in Docker or your cloud provider.

---

## Environment Variables
Each service uses its own `application.yml` for configuration. Key variables:
- `rabbitmq.exchange.name`
- `rabbitmq.queue.name`
- `rabbitmq.routing.key`
- `rabbitmq.update.queue.name`
- `rabbitmq.update.routing.key`
- `rabbitmq.delete.queue.name`
- `rabbitmq.delete.routing.key`
- `spring.datasource.*` (for DB config)
- `jwt.secret` (for auth)
Set these in your `application.yml` or as environment variables.

---

## Development & Testing
- **Backend:** Use `./mvnw spring-boot:run` in each service folder.
- **Frontend:** Use `npm run dev`.
- **Testing:** Use JUnit for backend, React Testing Library/Jest for frontend.
- **Linting:** Use ESLint for frontend, Checkstyle for backend.

---

## Troubleshooting
- **RabbitMQ exchange type error:** Ensure exchange type matches in config and RabbitMQ.
- **Missing environment variables:** Check `application.yml` in each service.
- **Service not registering:** Check Eureka and service logs.
- **Frontend API errors:** Check gateway and backend logs.

---

## Contributing
1. Fork the repo
2. Create a feature branch
3. Commit your changes
4. Open a pull request

---


