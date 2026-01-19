# SecureAuth API

A **secure, high-performance authentication and user management API** built with **Java 21, Spring Boot 3.2.1, PostgreSQL, and Redis**.  
Designed for **production-grade security, scalability, and maintainability**.

---

## 🏗️ Project Overview

This project demonstrates a **full-featured backend system** that includes:

- JWT-based authentication with **refresh token rotation**
- **Rate limiting** using Redis
- **Redis caching** for performance
- Handling **10k+ users efficiently** with pagination
- **Global exception handling** with consistent API responses

---

## ⚡ Features

### 1. Authentication

- **Register**: Create new user accounts
- **Login**: Secure login with JWT access token
- **Refresh Token Rotation**: Securely rotate refresh tokens, detect reuse, revoke sessions
- **Logout**: Invalidate all refresh tokens

### 2. User Management

- **Get user by ID** (cached)
- **Get all users** (paginated, cache-enabled)
- **Update user profile**
- **Role-based access control** (e.g., `ADMIN` can list all users)

### 3. Rate Limiting

- Redis-backed **IP-based rate limiting**
- Prevent brute-force login or abuse of refresh tokens
- Returns **HTTP 429** when limit exceeded

### 4. Caching & Performance

- **Redis caching** for frequently accessed data
- Pagination ensures **efficient handling of 10k+ records**
- Cache eviction on updates keeps data **consistent**

### 5. Exception Handling

- **Global exception handler** using `@RestControllerAdvice`
- Standardized error responses via `ApiError` DTO
- Handles:
  - 400: Validation errors
  - 401: Invalid credentials or refresh token
  - 403: Access denied
  - 404: Resource not found
  - 500: Internal server errors

---

## 🏛️ Architecture

Client -> RateLimitInterceptor (Redis check per IP) -> AuthController -> AuthService -> JWT Service & RefreshTokenRepository (PostgreSQL + hashed tokens) -> Redis (Cache)


## 🛠️ Tech Stack

- **Backend**: Java 21, Spring Boot 3.2.1
- **Database**: PostgreSQL 18.1
- **Cache & Rate Limiting**: Redis
- **Authentication**: JWT + Refresh Tokens
- **Validation**: Jakarta Bean Validation
- **Testing**: JUnit 5, Mockito

---

## 🔑 Design Highlights

1. **JWT + Refresh Token Rotation**
   - Short-lived JWT access tokens
   - Long-lived refresh tokens stored **hashed**
   - Tokens rotated on use, reuse detection prevents replay attacks

2. **Rate Limiting**
   - Implemented as **Spring MVC interceptor**
   - Redis ensures **atomic increment & TTL** for each IP

3. **Caching**
   - Redis caching for **read-heavy endpoints**
   - `@Cacheable` for `getUserById` and paginated `getAllUsers`
   - `@CacheEvict` on updates to prevent stale data

4. **Scalability**
   - Pagination (`PageRequest`) prevents loading all 10k+ users at once
   - Redis caching reduces database load
   - Stateless JWT + refresh tokens make the system horizontally scalable

5. **Exception Handling**
   - Global handler standardizes error responses
   - Improves **API professionalism** and **developer experience**

---

## 🚀 Running the Project Locally

1. **Clone the repository**

  - git clone <repo-url>
  - cd secureauth-api
  - Configure application.yml
    spring:
      datasource:
        url: jdbc:postgresql://localhost:5432/secureauth
        username: postgres
        password: your-pg-password
    
      data:
        redis:
          host: localhost
          port: 6379
    
    security:
      jwt:
        refresh-token-expiration: 604800000
    
2. **Run with Maven**

  - mvn clean install
  - mvn spring-boot:run
