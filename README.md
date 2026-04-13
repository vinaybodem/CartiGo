# CartiGo Microservices Architecture

CartiGo is a full-stack e-commerce order management system that supports admin, seller, and customer roles, enabling product management, order processing, and secure authentication. 

This repository contains the backend microservices built with Spring Boot, Spring Cloud, and various supporting infrastructure components.

## 🏗 System Architecture

The CartiGo backend is structured as a distributed microservices environment. All service communication is routed through an API Gateway, with internal services dynamically registering to a central Service Registry. Configurations are externalized via a Config Server. Message-driven asynchronous processes run via Apache Kafka.

### Infrastructure Services
- **`cartigo-api-gateway` (Port 8080)**: The single entry point for frontend clients. Handles request routing, CORS, security, and load balancing across microservices.
- **`cartigo-service-registry` (Port 8761)**: Eureka Server for dynamic service discovery. Required for services to locate and communicate with each other without hardcoded IPs. 
- **`cartigo-config-server` (Port 8888)**: Centralized configuration server pointing to `config-repo`, feeding properties to all microservices gracefully upon startup.

### Core Business Services
- **`cartigo-auth-service` (Port 8081)**: Manages secure access, authentication, and JWT token issuance.
- **`cartigo-user-service` (Port 8082)**: Manages user profiles across different roles (Admin, Seller, Customer).
- **`cartigo-product-service` (Port 8083)**: Handles the central product catalog, including product details, pricing, and active status.
- **`cartigo-category-service` (Port 8084)**: Maintains hierarchical category structuring for products.
- **`cartigo-inventory-service` (Port 8085)**: Manages stock levels in real-time, handling stock reservation during order processing.
- **`cartigo-cart-service` (Port 8086)**: Manages user shopping carts and checkout sessions.
- **`cartigo-notification-service` (Port 8087)**: Asynchronous consumer listening to Kafka topics to send out email notifications to users.
- **`cartigo-order-service` (Port 9005)**: Coordinates order placement, workflow, state transitions, and asynchronous event publishing to Kafka.
- **`cartigo-payment-service` (Port 9006)**: Payment initiation and verification, integrated with third-party gateways (e.g., Razorpay).
- **`cartigo-review-service` (Port 9007)**: Manages customer reviews and product ratings.
- **`cartigo-return-refund-service` (Port 9008)**: Dedicated service for post-purchase workflows (returns, exchanges, refunds).

### Shared Modules
- **`cartigo-common`**: A library shared across microservices containing common DTOs, cross-cutting configurations, exception handlers, and utility logics.

## 🛠 Tech Stack & Tools

- **Language & Frameworks**: Java 17, Spring Boot, Spring Cloud (Eureka, Config, Gateway)
- **Database**: MySQL 8.4 (Central cluster handling schemas for multiple services)
- **Message Broker**: Apache Kafka in KRaft Mode (Event-driven notifications)
- **Security**: JWT Tokens, Spring Configuration
- **API Documentation**: OpenAPI / Swagger (`springdoc-openapi`)
- **Containerization**: Docker & Docker Compose for rapid testing and isolated deployments

## 🚀 Running the Services Locally

You can spin up the required infrastructure (Database, Kafka, Service Registry, Gateway, Config Server) and individual microservices via the provided `docker-compose.yml`.

```bash
# Optional: Create environment variables file for secrets
cp .env.example .env

# Build the Maven project
./mvnw clean install -DskipTests

# Start the dockerized environment
docker-compose up -d --build
```

**Startup Sequence (handled by `depends_on` and health checks):**
1. Infrastructure backing services: `mysql`, `kafka`
2. Configuration provider: `cartigo-config-server`
3. Registration discovery: `cartigo-service-registry`
4. Routing and microservices: Gateway, Auth, Product, Cart, etc.
