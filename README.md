# Cloud Demo - Backend

Microservices backend with API Gateway, Task Service, and User Service.

## Architecture

```
Gateway (8080) ─┬─► Task Service (9090) ─► PostgreSQL
                └─► User Service (9091) ─► MongoDB
```

## Prerequisites

- Java 21
- Docker (for databases)
- Gradle

## Local Setup

### 1. Start Databases

```bash
# PostgreSQL
docker run -d --name postgres \
  -e POSTGRES_DB=TaskDB \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=master \
  -p 5432:5432 \
  postgres:15

# MongoDB
docker run -d --name mongodb \
  -e MONGO_INITDB_ROOT_USERNAME=admin \
  -e MONGO_INITDB_ROOT_PASSWORD=admin \
  -p 27017:27017 \
  mongo:7
```

Or use Docker Compose:

```bash
docker-compose up -d postgres mongodb
```

### 2. Start Services

Run each service in a separate terminal:

```bash
# Terminal 1: User Service (port 9091)
./gradlew :gke-api-user:bootRun

# Terminal 2: Task Service (port 9090)
./gradlew :gke-api-task:bootRun

# Terminal 3: Gateway (port 8080)
./gradlew :gke-api-gateway:bootRun
```

### 3. Verify

```bash
# Create a user
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"name": "John Doe"}'

# Create a task (use the user ID from above)
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{"userId": "USER_ID", "title": "My First Task"}'

# Get all tasks
curl http://localhost:8080/api/tasks
```

## Project Structure

```
cloud/
├── gke-api-gateway/    # Spring Cloud Gateway (port 8080)
├── gke-api-task/       # Task Service (port 9090, PostgreSQL)
├── gke-api-user/       # User Service (port 9091, MongoDB)
├── kustomize/          # Kubernetes manifests
└── docker-compose.yml
```
