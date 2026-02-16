# Authentication API - Quick Start Guide

## Prerequisites
- MySQL 8.0 running on localhost:3306
- Java 8 or higher
- Maven 3.6+

## Step 1: Database Setup

Create the database:
```sql
CREATE DATABASE IF NOT EXISTS uba_account_db;
```

## Step 2: Configure Application

The application is already configured in `application.yml`. Default settings:
- Server Port: 8082
- Database: localhost:3306/uba_account_db
- Database User: root
- Database Password: root
- JWT Secret: (configured in application.yml)
- JWT Expiration: 24 hours

## Step 3: Build and Run

```bash
cd D:\Projects\new\FiservUBADemo
mvn clean install
mvn spring-boot:run
```

Or using the compiled JAR:
```bash
mvn clean package
java -jar target/account-service-0.0.1-SNAPSHOT.jar
```

## Step 4: Test Authentication

### 1. Login as Admin

**Request:**
```bash
curl -X POST http://localhost:8082/api/v1/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"admin\",\"password\":\"Admin@123\"}"
```

**Expected Response:**
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
    "permissions": ["account:create", "account:read", ...]
  }
}
```

**Copy the `accessToken` value for subsequent requests.**

### 2. Get Current User Info

```bash
curl -X GET http://localhost:8082/api/v1/auth/me ^
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

### 3. Create an Account (Requires ADMIN/MANAGER role)

```bash
curl -X POST http://localhost:8082/api/v1/accounts ^
  -H "Content-Type: application/json" ^
  -H "Authorization: Bearer YOUR_TOKEN_HERE" ^
  -d "{\"customerId\":1001,\"accountType\":\"SAVINGS\",\"currency\":\"USD\",\"initialBalance\":5000.00}"
```

### 4. Get Account Details

```bash
curl -X GET "http://localhost:8082/api/v1/accounts/ACC-1001-001" ^
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

### 5. Test Authorization - Login as Regular User

```bash
curl -X POST http://localhost:8082/api/v1/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"user1\",\"password\":\"User@123\"}"
```

Try to create an account with USER role (should fail with 403):
```bash
curl -X POST http://localhost:8082/api/v1/accounts ^
  -H "Content-Type: application/json" ^
  -H "Authorization: Bearer USER_TOKEN_HERE" ^
  -d "{\"customerId\":1002,\"accountType\":\"CHECKING\",\"currency\":\"USD\",\"initialBalance\":1000.00}"
```

## Test Users

| Username | Password | Role | Permissions |
|----------|----------|------|-------------|
| admin | Admin@123 | ADMIN | All permissions |
| manager1 | Manager@123 | MANAGER | Account & transaction management |
| user1 | User@123 | USER | Basic access |
| customer1 | Customer@123 | CUSTOMER | Read-only access |

**Note:** All passwords are BCrypt encoded in the database with strength 10. The migration file (V1.2) contains the correct hashes that match these documented passwords.

## Testing Different Scenarios

### Scenario 1: Admin Full Access
1. Login as `admin`
2. Create account ✓
3. Freeze account ✓
4. Unfreeze account ✓
5. Perform transactions ✓

### Scenario 2: Manager Limited Access
1. Login as `manager1`
2. Create account ✓
3. Freeze account ✓
4. Perform transactions ✓
5. Create/delete users ✗ (403 Forbidden)

### Scenario 3: User Basic Access
1. Login as `user1`
2. View accounts ✓
3. View transactions ✓
4. Create account ✗ (403 Forbidden)
5. Freeze account ✗ (403 Forbidden)

### Scenario 4: Customer Read-Only
1. Login as `customer1`
2. View own accounts ✓
3. View transactions ✓
4. Perform any write operation ✗ (403 Forbidden)

## Common Issues

### 1. Database Connection Error
- Ensure MySQL is running
- Check credentials in application.yml
- Verify database exists

### 2. JWT Token Invalid
- Token may have expired (24 hours)
- Re-login to get new token
- Check JWT_SECRET is consistent

### 3. 403 Forbidden
- User doesn't have required role/permission
- Check the endpoint's authorization requirements
- Verify JWT token is included in Authorization header

### 4. 401 Unauthorized
- JWT token is missing or invalid
- Include: `Authorization: Bearer YOUR_TOKEN`
- Ensure token hasn't expired

## API Endpoints Summary

### Authentication Endpoints (No auth required)
- POST `/api/v1/auth/login` - User login
- POST `/api/v1/auth/register` - User registration

### Protected Endpoints (Auth required)
- GET `/api/v1/auth/me` - Get current user
- POST `/api/v1/auth/logout` - User logout

### Account Endpoints (Role-based access)
- POST `/api/v1/accounts` - Create account (ADMIN, MANAGER)
- GET `/api/v1/accounts/{accountNumber}` - Get account (All authenticated)
- GET `/api/v1/accounts/customer/{customerId}` - Get customer accounts (All authenticated)
- POST `/api/v1/accounts/debit` - Debit account (ADMIN, MANAGER)
- POST `/api/v1/accounts/credit` - Credit account (ADMIN, MANAGER)
- PUT `/api/v1/accounts/{accountNumber}/freeze` - Freeze account (ADMIN)
- PUT `/api/v1/accounts/{accountNumber}/unfreeze` - Unfreeze account (ADMIN)

## Postman Collection

Import these into Postman for easier testing:

1. **Environment Variables:**
   - `baseUrl`: http://localhost:8082
   - `token`: (set after login)

2. **Pre-request Script for authenticated endpoints:**
```javascript
pm.request.headers.add({
    key: 'Authorization',
    value: 'Bearer ' + pm.environment.get('token')
});
```

## Next Steps

1. Review complete API documentation: `AUTHENTICATION_API.md`
2. Check all available endpoints: `API_ENDPOINTS.md`
3. Review security configuration: `src/main/java/com/fiserv/uba/account/config/SecurityConfig.java`
4. Customize roles and permissions as needed

---

**Happy Testing!** 🚀

