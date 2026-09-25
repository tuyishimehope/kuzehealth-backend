# KuzaHealth Backend

![Java](https://img.shields.io/badge/Java-17-007396.svg?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3-brightgreen?logo=springboot&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-3.x-C71A36?logo=apachemaven&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-ready-2496ED?logo=docker&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-13%2B-336791?logo=postgresql&logoColor=white)
![OpenAPI](https://img.shields.io/badge/OpenAPI-3.0-6BA539?logo=swagger&logoColor=white)
![Render](https://img.shields.io/badge/Deployed%20on-Render-46E3B7?logo=render&logoColor=white)

A production-ready Spring Boot backend for a maternal health application built with Domain-Driven Design (DDD). It provides APIs for users, health workers, parents, and related services, secured with JWT and documented via OpenAPI.


## Contents
- Features
- Tech Stack
- Quick Start
  - Run with Docker Compose
  - Run locally with Maven
- Configuration (Environment Variables)
- API Documentation and Health
- CI/CD and Deployment
- Project Structure
- Contributing
- License


## Features
- RESTful APIs with Spring Boot 3
- PostgreSQL persistence with Spring Data JPA and HikariCP
- JWT-based authentication and authorization
- Validation (Jakarta Validation)
- Email sending (Spring Mail)
- API docs via Springdoc OpenAPI UI
- Actuator health, info, and metrics endpoints
- Containerized with Docker; local stack via docker-compose
- GitHub Actions workflow to trigger Render deploys


## Tech Stack
- Java 17
- Spring Boot 3.3.x (Web, Data JPA, Security, Validation, Mail, Actuator)
- PostgreSQL 13+
- HikariCP connection pool
- Maven build
- Docker & Docker Compose
- OpenAPI (springdoc-openapi)


## Quick Start
Prerequisites: Java 17, Maven 3.9+, Docker 20+, Docker Compose.

### 1) Run with Docker Compose (recommended for local)
This starts PostgreSQL and the app together with sensible defaults for development.

```bash
# From the project root
docker compose up --build
```

- App: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html (or http://localhost:8080/swagger-ui/index.html)
- Health: http://localhost:8080/actuator/health

Environment used by docker-compose is defined in docker-compose.yml and sets the Spring profile to dev. Seeders run only on the dev profile.

### 2) Run locally with Maven (using your own Postgres)
Start PostgreSQL locally and export environment variables (matching application.properties):

```bash
export SPRING_DATASOURCE_URL='jdbc:postgresql://localhost:5432/kuzahealth'
export SPRING_DATASOURCE_USERNAME='postgres'
export SPRING_DATASOURCE_PASSWORD='<your-password>'
export SPRING_PROFILES_ACTIVE='dev'
export JWT_SECRET='replace-with-hex-64'
export JWT_EXPIRATION='86400'
export SPRING_MAIL_USERNAME='your@gmail.com'
export SPRING_MAIL_PASSWORD='your-app-password'
export PINDO_TOKEN='<your-token>'
export CORS_ORIGINS='http://localhost:5173'

# Run the app
mvn spring-boot:run
```

The server starts on port 8080 by default (or the PORT env var if provided).


## Configuration (Environment Variables)
Key properties (see src/main/resources/application.properties):

- Server
  - PORT: External port (Render sets this automatically)
  - SPRING_PROFILES_ACTIVE: prod by default; use dev locally

- Database
  - SPRING_DATASOURCE_URL, SPRING_DATASOURCE_USERNAME, SPRING_DATASOURCE_PASSWORD
  - Hikari defaults tuned for low resource usage (max pool size = 2 in prod defaults)

- Security (JWT)
  - JWT_SECRET: Hex/base64 secret
  - JWT_EXPIRATION: Token lifetime in seconds (default 86400)

- Mail
  - SPRING_MAIL_USERNAME, SPRING_MAIL_PASSWORD

- SMS / Pindo
  - PINDO_TOKEN

- CORS
  - CORS_ORIGINS: Comma-separated allowed origins for Actuator endpoints


## API Documentation and Health
- Swagger UI: /swagger-ui.html or /swagger-ui/index.html
- OpenAPI JSON: /v3/api-docs
- Actuator Health: /actuator/health
- Liveness/Readiness: /actuator/health/liveness and /actuator/health/readiness


## CI/CD and Deployment
- GitHub Actions: .github/workflows/render-deploy.yml builds on push to main and triggers a Render deploy hook.
  - Set repository secret RENDER_DEPLOY_HOOK to your Render Deploy Hook URL for your service.
  - Fallback: the workflow includes a hardcoded hook URL which can be replaced or removed if you prefer secrets-only.
- Dockerfile: Multi-stage build producing a containerized app optimized for container resources (JAVA_OPTS enables container-aware memory limits).
- Render: Ensure environment variables are configured in the Render dashboard. The application binds to $PORT via server.port=${PORT:8080}.


## Project Structure
- src/main/java/...: Application code (controllers, entities, repositories, etc.)
- src/main/resources: application.properties, data seed files
- Dockerfile: Container image definition
- docker-compose.yml: Local dev stack (Postgres + app)
- .github/workflows/render-deploy.yml: CI to build and trigger Render deploy


## Contributing
1. Fork the repository
2. Create a feature branch: git checkout -b feat/your-feature
3. Commit changes: git commit -m "feat: add your feature"
4. Push to branch: git push origin feat/your-feature
5. Open a Pull Request

Please follow conventional commits if possible.
