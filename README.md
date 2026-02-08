# Cartigo Backend (Microservices) — Ready-to-Run

This repository is a **working Spring Boot + Spring Cloud microservices backend** for **Cartigo** (multi-vendor e-commerce).

It includes:
- **Config Server** (central configs)
- **Service Registry** (Eureka)
- **API Gateway** (single entrypoint + JWT check)
- Core e-commerce services: **Auth, User, Product, Inventory, Cart, Order, Payment (Razorpay), Review (verified purchase), Return/Refund**
- Admin/Reporting/Notification/Search services are included (minimal), and can be extended.

## 1) Start everything with Docker (recommended)

From the repo root (`cartigo-backend/`):

```bash
docker compose up --build
```

That will start:
- MySQL (root/root)
- Redis
- OpenSearch
- Config Server (8888)
- Eureka (8761)
- API Gateway (8080)
- All services

### API base URL
Use the **gateway**:
- `http://localhost:8080`

### First Admin (bootstrapped)
On first startup, the auth-service seeds the first admin if no admin exists.
Defaults (change in docker-compose.yml env):
- email: `admin@cartigo.com`
- password: `Admin@123`

## 2) Razorpay setup
In `docker-compose.yml` set:
- `RAZORPAY_KEY_ID`
- `RAZORPAY_KEY_SECRET`
- `RAZORPAY_WEBHOOK_SECRET`

If you leave them empty, payment endpoints run in **mock mode** (you can still test order flow).

## 3) Quick test flow (Postman)

1) Register a customer:
- `POST /auth/register`

2) Login:
- `POST /auth/login` (copy JWT)

3) Create product as seller (register seller, then use sellerId):
- `POST /products`

4) Add to cart:
- `POST /cart/{userId}/items`

5) Create payment intent:
- `POST /payments/create`

6) Create order:
- `POST /orders`

7) Review (after delivery):
- `POST /reviews`

8) Return/refund request:
- `POST /returns`

## 4) Service ports (direct access)
Gateway is recommended, but you can also access services directly:
- config-server: 8888
- service-registry: 8761
- api-gateway: 8080
- auth-service: 9001
- user-service: 9002
- product-service: 9003
- inventory-service: 9004
- order-service: 9005
- payment-service: 9006
- review-service: 9007
- return-refund-service: 9008
- admin-service: 9009
- reporting-analytics-service: 9010
- notification-service: 9011
- audit-logging-service: 9012
- search-service: 9013
- cart-service: 9014

