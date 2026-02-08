# Cartigo - User Service (Standalone)

This service manages:
- `User` (login identity: email, password, role)
- Role profiles using shared primary key one-to-one:
  - `Customer`
  - `Seller`
  - `Admin`

## Why Shared Primary Key (`@MapsId`)?
A profile row uses the same ID as the User. Example:
- users.id = 5
- customers.id = 5 (FK to users.id)
This guarantees 1-to-1 mapping without extra foreign keys.

## Run

1) Create MySQL database (optional - auto creates)
- DB: `cartigo_users`
- user/pass in `application.yml`

2) Start the service
```bash
mvn spring-boot:run
```

Swagger:
- http://localhost:8082/swagger-ui/index.html

## APIs

### Create user
POST `http://localhost:8082/api/users`
```json
{
  "email": "john@example.com",
  "password": "Password@123",
  "role": "CUSTOMER"
}
```

### Create customer profile
POST `http://localhost:8082/api/customers/{userId}`

### Create seller profile
POST `http://localhost:8082/api/sellers/{userId}`

### Create admin profile
POST `http://localhost:8082/api/admins/{userId}`

## Config package (simple)
- `SecurityConfig`: Password hashing + endpoint security rules
- `WebConfig`: CORS rules so frontend can call the APIs
