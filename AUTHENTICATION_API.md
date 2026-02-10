# Authentication API Documentation

## Overview
This document describes the Authentication and Authorization API endpoints for the Fiserv UBA (Unified Banking Application). The API uses JWT (JSON Web Tokens) for authentication with claims-based authorization.

## Base URL
```
http://localhost:8082/api/v1
```

## Authentication Flow
1. User logs in with username/password
2. Server validates credentials and generates JWT token with claims
3. JWT token contains: userId, username, email, customerId, roles, and permissions
4. Client includes JWT token in Authorization header for subsequent requests
5. Server validates token and checks permissions for each request

---

## Authentication Endpoints

### 1. User Login
**Endpoint:** `POST /api/v1/auth/login`

**Description:** Authenticates user and returns JWT token with claims

**Request Body:**
```json
{
  "username": "admin",
  "password": "Admin@123"
}
```

**Success Response (200 OK):**
```json
{
  "status": 200,
  "message": "Login successful",
  "data": {
    "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
    "tokenType": "Bearer",
    "expiresIn": 86400,
    "username": "admin",
    "email": "admin@fiserv.com",
    "customerId": null,
    "roles": ["ADMIN"],
    "permissions": [
      "account:create",
      "account:read",
      "account:update",
      "account:delete",
      "account:freeze",
      "transaction:create",
      "transaction:read",
      "transaction:write",
      "transaction:reverse",
      "user:create",
      "user:read",
      "user:update",
      "user:delete",
      "report:read",
      "report:generate"
    ]
  }
}
```

**Error Response (401 Unauthorized):**
```json
{
  "status": 401,
  "message": "Invalid username or password",
  "data": null
}
```

---

### 2. User Registration
**Endpoint:** `POST /api/v1/auth/register`

**Description:** Registers a new user account

**Request Body:**
```json
{
  "username": "newuser",
  "email": "newuser@fiserv.com",
  "password": "SecurePass@123",
  "firstName": "John",
  "lastName": "Doe",
  "phoneNumber": "+1234567890",
  "customerId": 2001,
  "roles": ["USER"]
}
```

**Success Response (201 Created):**
```json
{
  "status": 201,
  "message": "User registered successfully",
  "data": {
    "id": 5,
    "username": "newuser",
    "email": "newuser@fiserv.com",
    "firstName": "John",
    "lastName": "Doe",
    "phoneNumber": "+1234567890",
    "customerId": 2001,
    "enabled": true,
    "lastLogin": null,
    "createdAt": "2026-02-10T10:30:00",
    "roles": ["USER"],
    "permissions": [
      "account:read",
      "transaction:create",
      "transaction:read"
    ]
  }
}
```

**Error Response (400 Bad Request):**
```json
{
  "status": 400,
  "message": "Username is already taken",
  "data": null
}
```

---

### 3. Get Current User
**Endpoint:** `GET /api/v1/auth/me`

**Description:** Returns authenticated user details

**Headers:**
```
Authorization: Bearer <JWT_TOKEN>
```

**Success Response (200 OK):**
```json
{
  "status": 200,
  "message": "User details retrieved successfully",
  "data": {
    "id": 1,
    "username": "admin",
    "email": "admin@fiserv.com",
    "firstName": "System",
    "lastName": "Administrator",
    "phoneNumber": null,
    "customerId": null,
    "enabled": true,
    "lastLogin": "2026-02-10T09:15:30",
    "createdAt": "2026-02-10T08:00:00",
    "roles": ["ADMIN"],
    "permissions": [
      "account:create",
      "account:read",
      "transaction:write",
      "..."
    ]
  }
}
```

---

### 4. User Logout
**Endpoint:** `POST /api/v1/auth/logout`

**Description:** Logs out the user (client should remove token)

**Headers:**
```
Authorization: Bearer <JWT_TOKEN>
```

**Success Response (200 OK):**
```json
{
  "status": 200,
  "message": "Logout successful. Please remove the token from client.",
  "data": null
}
```

---

## JWT Token Structure

### Token Claims
The JWT token contains the following claims:

```json
{
  "sub": "admin",
  "userId": 1,
  "username": "admin",
  "email": "admin@fiserv.com",
  "customerId": null,
  "roles": ["ADMIN"],
  "permissions": [
    "account:create",
    "account:read",
    "account:update",
    "account:delete",
    "account:freeze",
    "transaction:create",
    "transaction:read",
    "transaction:write",
    "transaction:reverse",
    "user:create",
    "user:read",
    "user:update",
    "user:delete",
    "report:read",
    "report:generate"
  ],
  "enabled": true,
  "iat": 1707559200,
  "exp": 1707645600
}
```

### Using JWT Token
Include the token in the Authorization header:
```
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsInVzZXJJZCI6MSwidXNlcm5hbWUiOiJhZG1pbiIsImVtYWlsIjoiYWRtaW5AZmlzZXJ2LmNvbSIsImN1c3RvbWVySWQiOm51bGwsInJvbGVzIjpbIkFETUlOIl0sInBlcm1pc3Npb25zIjpbImFjY291bnQ6Y3JlYXRlIiwiYWNjb3VudDpyZWFkIl0sImVuYWJsZWQiOnRydWUsImlhdCI6MTcwNzU1OTIwMCwiZXhwIjoxNzA3NjQ1NjAwfQ.signature
```

---

## Roles and Permissions

### Available Roles

| Role | Description |
|------|-------------|
| ADMIN | Administrator with full access to all resources |
| MANAGER | Manager with elevated permissions for account and transaction management |
| USER | Regular user with basic account and transaction access |
| CUSTOMER | Customer with read-only access to their accounts |

### Available Permissions

#### Account Permissions
- `account:create` - Create new accounts
- `account:read` - Read account information
- `account:update` - Update account information
- `account:delete` - Delete accounts
- `account:freeze` - Freeze/unfreeze accounts

#### Transaction Permissions
- `transaction:create` - Create transactions
- `transaction:read` - Read transaction history
- `transaction:write` - Perform debit/credit operations
- `transaction:reverse` - Reverse transactions

#### User Permissions
- `user:create` - Create new users
- `user:read` - Read user information
- `user:update` - Update user information
- `user:delete` - Delete users

#### Report Permissions
- `report:read` - View reports
- `report:generate` - Generate reports

### Role-Permission Matrix

| Permission | ADMIN | MANAGER | USER | CUSTOMER |
|------------|-------|---------|------|----------|
| account:create | ✓ | ✓ | ✗ | ✗ |
| account:read | ✓ | ✓ | ✓ | ✓ |
| account:update | ✓ | ✓ | ✗ | ✗ |
| account:delete | ✓ | ✗ | ✗ | ✗ |
| account:freeze | ✓ | ✓ | ✗ | ✗ |
| transaction:create | ✓ | ✓ | ✓ | ✗ |
| transaction:read | ✓ | ✓ | ✓ | ✓ |
| transaction:write | ✓ | ✓ | ✗ | ✗ |
| transaction:reverse | ✓ | ✗ | ✗ | ✗ |
| user:create | ✓ | ✗ | ✗ | ✗ |
| user:read | ✓ | ✗ | ✗ | ✗ |
| user:update | ✓ | ✗ | ✗ | ✗ |
| user:delete | ✓ | ✗ | ✗ | ✗ |
| report:read | ✓ | ✓ | ✗ | ✗ |
| report:generate | ✓ | ✓ | ✗ | ✗ |

---

## Test Users

The following test users are created by default:

| Username | Password | Role | Customer ID |
|----------|----------|------|-------------|
| admin | Admin@123 | ADMIN | null |
| manager1 | Manager@123 | MANAGER | 1001 |
| user1 | User@123 | USER | 1002 |
| customer1 | Customer@123 | CUSTOMER | 1003 |

---

## Protected Account Endpoints

### Authorization Requirements

| Endpoint | Method | Required Roles/Permissions |
|----------|--------|---------------------------|
| /api/v1/accounts | POST | ADMIN, MANAGER or account:create |
| /api/v1/accounts/{accountNumber} | GET | ADMIN, MANAGER, USER or account:read |
| /api/v1/accounts/customer/{customerId} | GET | ADMIN, MANAGER, USER or account:read |
| /api/v1/accounts/debit | POST | ADMIN, MANAGER or transaction:write |
| /api/v1/accounts/credit | POST | ADMIN, MANAGER or transaction:write |
| /api/v1/accounts/{accountNumber}/freeze | PUT | ADMIN or account:freeze |
| /api/v1/accounts/{accountNumber}/unfreeze | PUT | ADMIN or account:freeze |

---

## Example Usage with cURL

### Login
```bash
curl -X POST http://localhost:8082/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "Admin@123"
  }'
```

### Create Account (with JWT)
```bash
curl -X POST http://localhost:8082/api/v1/accounts \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "customerId": 1001,
    "accountType": "SAVINGS",
    "currency": "USD",
    "initialBalance": 1000.00
  }'
```

### Get Current User
```bash
curl -X GET http://localhost:8082/api/v1/auth/me \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

---

## Error Codes

| Status Code | Description |
|-------------|-------------|
| 200 | Success |
| 201 | Created |
| 400 | Bad Request - Invalid input |
| 401 | Unauthorized - Invalid or missing token |
| 403 | Forbidden - Insufficient permissions |
| 404 | Not Found |
| 500 | Internal Server Error |

---

## Security Best Practices

1. **Store JWT securely** - Use httpOnly cookies or secure storage
2. **Token expiration** - Default: 24 hours (86400000 ms)
3. **HTTPS only** - Always use HTTPS in production
4. **Rotate secrets** - Change JWT_SECRET regularly
5. **Password policy** - Minimum 6 characters (recommended: 8+ with special chars)
6. **Rate limiting** - Implement on login endpoint
7. **Logout handling** - Remove token from client storage

---

## Configuration

Update `application.yml` with your JWT settings:

```yaml
spring:
  security:
    jwt:
      secret: ${JWT_SECRET:your-secret-key-change-in-production-min-256-bits}
      expiration: ${JWT_EXPIRATION:86400000}  # 24 hours in milliseconds
```

**Important:** Change the JWT secret in production and use at least 256 bits (32 characters).

---

**Last Updated:** February 10, 2026  
**Version:** 1.0.0

