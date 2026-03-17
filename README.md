# Spring Boot E-commerce API
[![CI Pipeline](https://github.com/egjarabo/spring-boot-ecommerce-api/actions/workflows/ci.yml/badge.svg)](https://github.com/egjarabo/spring-boot-ecommerce-api/actions/workflows/ci.yml)
[![Coverage](https://img.shields.io/badge/coverage-89%25-brightgreen)](target/site/jacoco/index.html)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=egjarabo_spring-boot-ecommerce-api&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=egjarabo_spring-boot-ecommerce-api)

REST API for an e-commerce platform built with Spring Boot 3, Java 21,
JPA and PostgreSQL. Demonstrates modern Java and Spring practices including
Records, integration testing with Testcontainers, architecture validation
with ArchUnit and code coverage with JaCoCo.

## Features

- **Product management** — full CRUD with validation
- **Order management** — order lifecycle with product snapshot pattern
- **Integration tests** — real PostgreSQL via Testcontainers
- **Architecture validation** — layer rules enforced with ArchUnit
- **Code coverage** — 89% line coverage enforced via JaCoCo (minimum 70%)
- **Containerization** — multi-stage Dockerfile
- **Health monitoring** — Spring Boot Actuator

## Tech stack

| Technology | Version | Purpose |
|---|---|---|
| Java | 21 | Language |
| Spring Boot | 3.x | Framework |
| Spring Data JPA | 3.x | Data access |
| PostgreSQL | 16 | Database |
| Testcontainers | Latest | Integration testing |
| ArchUnit | 1.3.0 | Architecture validation |
| JaCoCo | 0.8.11 | Code coverage |
| Docker | - | Containerization |
| Lombok | Latest | Boilerplate reduction |

## Architecture
```
com.egjarabo.ecommerce
├── product         # Product domain — CRUD
│   └── dto         # ProductRequest, ProductResponse (Records)
├── order           # Order domain — lifecycle management
│   └── dto         # OrderRequest, OrderResponse (Records)
└── common
    └── exception   # ResourceNotFoundException
```

### Architecture rules (ArchUnit)

The following rules are validated automatically on every build:

- Controllers must not access repositories directly
- Services must not depend on controllers
- Controllers must not use JPA entities — only DTOs
- Repositories must only be accessed from services
- All classes named `*Controller` must be annotated with `@RestController`

## API endpoints

### Products
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/v1/products` | Get all products |
| GET | `/api/v1/products/{id}` | Get product by id |
| GET | `/api/v1/products/category/{category}` | Get by category |
| POST | `/api/v1/products` | Create product |
| PUT | `/api/v1/products/{id}` | Update product |
| DELETE | `/api/v1/products/{id}` | Delete product |

### Orders
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/v1/orders` | Get all orders |
| GET | `/api/v1/orders/{id}` | Get order by id |
| GET | `/api/v1/orders/customer/{customerId}` | Get by customer |
| POST | `/api/v1/orders` | Create order |
| PATCH | `/api/v1/orders/{id}/status` | Update order status |

## How to run

### Prerequisites
- Docker Desktop
- Java 21+
- Maven 3.9+

### Start database
```bash
docker compose up -d
```

### Run application
```bash
mvn spring-boot:run
```

### Run tests
```bash
mvn test
```

### Run tests with coverage report
```bash
mvn verify
# Report available at target/site/jacoco/index.html
```

### Build and run with Docker
```bash
docker build -t ecommerce-api:latest .
docker run -d \
  --network spring-boot-ecommerce-api_default \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://ecommerce-postgres:5432/ecommerce \
  -e SPRING_DATASOURCE_USERNAME=ecommerce \
  -e SPRING_DATASOURCE_PASSWORD=ecommerce \
  -p 8080:8080 \
  ecommerce-api:latest
```

### Health check
```
GET http://localhost:8080/actuator/health
```

## Key design decisions

- **Package by feature** — product and order domains are self-contained
- **Records for DTOs** — immutable, concise data carriers
- **Product snapshot** — order items store product name and price at
  purchase time, preserving historical accuracy
- **Testcontainers** — integration tests run against a real PostgreSQL
  instance, not mocks
- **ArchUnit** — architecture rules are enforced automatically at build
  time, preventing accidental layer violations
- **JaCoCo** — build fails if line coverage drops below 70%, ensuring
  test quality is maintained over time