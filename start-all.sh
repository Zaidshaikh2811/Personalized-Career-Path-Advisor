#!/bin/bash

# Start Docker Compose for DB, RabbitMQ, etc.
echo "Starting Docker Compose containers..."
docker-compose up -d

# Wait for DB and RabbitMQ to be healthy (adjust as needed)
echo "Waiting for DB and RabbitMQ to be ready..."
sleep 15

# Start Config Server
cd config_server
nohup ./mvnw spring-boot:run > config_server.log 2>&1 &
cd ..
echo "Config Server started."
sleep 10

# Start Eureka Server
cd eureka
nohup ./mvnw spring-boot:run > eureka.log 2>&1 &
cd ..
echo "Eureka Server started."
sleep 10

# Start Gateway
cd gateway
nohup ./mvnw spring-boot:run > gateway.log 2>&1 &
cd ..
echo "Gateway started."
sleep 10

# Start Activity Service
cd activity_service
nohup ./mvnw spring-boot:run > activity_service.log 2>&1 &
cd ..
echo "Activity Service started."
sleep 5

# Start AI Service
cd ai_service
nohup ./mvnw spring-boot:run > ai_service.log 2>&1 &
cd ..
echo "AI Service started."
sleep 5

# Start Auth Service
cd auth_service
nohup ./mvnw spring-boot:run > auth_service.log 2>&1 &
cd ..
echo "Auth Service started."
sleep 5

# Start User Service
cd user_service
nohup ./mvnw spring-boot:run > user_service.log 2>&1 &
cd ..
echo "User Service started."
sleep 5

echo "All microservices started. Check individual *.log files for output."

