# letsplay

A RESTful CRUD API for user and product management, built with Spring Boot, MongoDB, and JWT authentication.

## Features

- **User Management:**  
  - Register via `/auth/register`
  - Login via `/auth/login`
  - View/update/delete own profile via `/users/me`
  - Admin can manage all users (except admin cannot be deleted)
  - `POST /users` is admin-only (for admin to create users)
- **Product Management:**  
  - Create, read, update, delete products
  - Only owner or admin can modify/delete
  - Public can view products
- **Authentication:**  
  - JWT-based login and registration
  - Logout via `/auth/logout` (JWT token is blacklisted)
- **Authorization:**  
  - Role-based access (`USER`, `ADMIN`)
  - Only one admin, all others are users
- **Security:**  
  - BCrypt password hashing
  - Input validation
  - Sensitive info protection (no passwords in responses)
  - HTTPS ready
  - JWT token blacklist for logout
- **Error Handling:**  
  - Centralized exception handler
  - No 5XX errors, proper HTTP status codes
- **CORS:**  
  - Configured for local development
- **MongoDB:**  
  - Used for persistent storage

## Quick Start

### Prerequisites

- Java 17+
- Maven
- MongoDB (local or Docker: `docker run -d -p 27017:27017 mongo`)

### Running the Project

1. **Clone the repo:**
   ```sh
   git clone <your-repo-url>
   cd letsplay
   ```

2. **Start MongoDB** (if not running):
   ```sh
   docker run -d -p 27017:27017 mongo
   ```

3. **Build and run:**
   ```sh
   ./mvnw spring-boot:run
   ```

4. **API is available at:**  
   `https://localhost:8443` (HTTPS, self-signed cert for dev)

### API Endpoints

#### Auth

- `POST /auth/login` — Login, returns JWT
- `POST /auth/register` — Register, returns JWT
- `POST /auth/logout` — Logout, blacklists JWT token

#### Users

- `GET /users` — List all users (admin only)
- `GET /users/{id}` — Get user by ID (admin only)
- `GET /users/me` — Get current user's info
- `POST /users` — Create user (admin only)
- `PUT /users/{id}` — Update user (admin or self)
- `DELETE /users/{id}` — Delete user (admin only, cannot delete admin)
- `DELETE /users/me` — Delete own account

#### Products

- `GET /products` — List all products (public)
- `GET /products/{id}` — Get product by ID (public)
- `POST /products` — Create product (authenticated)
- `PUT /products/{id}` — Update product (owner or admin)
- `DELETE /products/{id}` — Delete product (owner or admin)
- `GET /products/my-products` — List current user's products

### Security

- **JWT:** All protected endpoints require `Authorization: Bearer <token>`.
- **Roles:** Only admin can manage users; users can only manage their own profile and products.
- **Password:** Hashed with BCrypt.
- **HTTPS:** Configured for production.
- **CORS:** Configured for local frontend development.
- **Logout:** Blacklists JWT token so it cannot be reused.

### Error Handling

- All errors return structured JSON with status, error, message, and path.
- No 5XX errors; all exceptions handled.

### Testing

- Use Postman or similar tool.
- Register/login to get JWT.
- Test all endpoints with and without JWT.
- Try error scenarios (invalid credentials, unauthorized access, etc.).
- Test logout: after calling `/auth/logout`, the token is blacklisted and cannot be used.

### Bonus

- **CORS:** Configured.
- **Rate Limiting:** Not implemented (can be added as a filter).

## Project Structure

```
letsplay/
├── src/
│   ├── main/
│   │   ├── java/com/toft/letsplay/
│   │   │   ├── model/        # User, Product
│   │   │   ├── repository/   # UserRepository, ProductRepository, BlacklistedTokenRepository
│   │   │   ├── service/      # UserService, ProductService
│   │   │   ├── controller/   # UserController, ProductController, AuthController
│   │   │   ├── security/     # SecurityConfig, JwtUtil, JwtAuthenticationFilter, CustomUserDetailsService, TokenBlacklist, BlacklistedToken
│   │   │   ├── exception/    # GlobalExceptionHandler, custom exceptions
│   │   │   ├── dto/          # UserDto, ProductDto
│   │   └── resources/
│   │       ├── application.properties
│   │       └── keystore.p12  # Self-signed cert for HTTPS
│   └── test/java/com/toft/letsplay/
│       └── LetsplayApplicationTests.java
├── .gitignore
├── pom.xml
├── README.md
└── TODO.md
```

## Generate a new self-signed certificate for development
```
keytool -genkeypair -alias letsplay -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore src/main/resources/keystore.p12 -validity 3650 -storepass changeit -dname "CN=localhost, OU=Development, O=Letsplay, L=City, ST=State, C=US"
```

## Requirements
- Java 17
- Maven
- MongoDB

---

**Ready for audit and production use.**