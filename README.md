# Spring Boot E-commerce API

REST API for an e-commerce platform built with Spring Boot 3, Java 21,
JPA and PostgreSQL. Demonstrates modern Java and Spring practices including
Records, integration testing with Testcontainers and
containerization with Docker.

## Features

- **Product management** — full CRUD with validation
- **Order management** — order lifecycle with product snapshot pattern
- **Integration tests** — real PostgreSQL via Testcontainers
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
```