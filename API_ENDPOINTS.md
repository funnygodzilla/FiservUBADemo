# Account Service REST API Endpoints

## Overview
This document describes all the REST API endpoints created for the Account Service in the FiservUBA application.

## Base URL
```
/api/v1/accounts
```

## Endpoints

### 1. Create Account
**HTTP Method:** `POST`
**Endpoint:** `/api/v1/accounts`
**Description:** Create a new account for a customer

**Request Body:**
```json
{
  "customerId": 1001,
  "accountType": "CHECKING",
  "initialBalance": 5000.00,
  "currency": "USD"
}
```

**Response (201 Created):**
```json
{
  "status": 201,
  "message": "Account created successfully",
  "data": {
    "id": 1,
    "accountNumber": "ACC1707210600ABCD1234",
    "customerId": 1001,
    "accountType": "CHECKING",
    "balance": 5000.00,
    "currency": "USD",
    "status": "ACTIVE",
    "createdAt": "2026-02-06T21:55:00",
    "updatedAt": "2026-02-06T21:55:00"
  },
  "timestamp": 1707210900000
}
```

---

### 2. Get Account by Account Number
**HTTP Method:** `GET`
**Endpoint:** `/api/v1/accounts/{accountNumber}`
**Description:** Retrieve account details by account number

**Path Parameters:**
- `accountNumber` (String): The account number to retrieve

**Response (200 OK):**
```json
{
  "status": 200,
  "message": "Account retrieved successfully",
  "data": {
    "id": 1,
    "accountNumber": "ACC1707210600ABCD1234",
    "customerId": 1001,
    "accountType": "CHECKING",
    "balance": 5000.00,
    "currency": "USD",
    "status": "ACTIVE",
    "createdAt": "2026-02-06T21:55:00",
    "updatedAt": "2026-02-06T21:55:00"
  },
  "timestamp": 1707210900000
}
```

---

### 3. Get Customer Accounts
**HTTP Method:** `GET`
**Endpoint:** `/api/v1/accounts/customer/{customerId}`
**Description:** Retrieve all accounts for a specific customer

**Path Parameters:**
- `customerId` (Long): The customer ID

**Response (200 OK):**
```json
{
  "status": 200,
  "message": "Customer accounts retrieved successfully",
  "data": [
    {
      "id": 1,
      "accountNumber": "ACC1707210600ABCD1234",
      "customerId": 1001,
      "accountType": "CHECKING",
      "balance": 5000.00,
      "currency": "USD",
      "status": "ACTIVE",
      "createdAt": "2026-02-06T21:55:00",
      "updatedAt": "2026-02-06T21:55:00"
    },
    {
      "id": 4,
      "accountNumber": "ACC1707210603MNOP3456",
      "customerId": 1001,
      "accountType": "BUSINESS",
      "balance": 50000.00,
      "currency": "USD",
      "status": "ACTIVE",
      "createdAt": "2026-02-06T21:55:00",
      "updatedAt": "2026-02-06T21:55:00"
    }
  ],
  "timestamp": 1707210900000
}
```

---

### 4. Debit Account
**HTTP Method:** `POST`
**Endpoint:** `/api/v1/accounts/debit`
**Description:** Debit funds from an account

**Request Body:**
```json
{
  "accountNumber": "ACC1707210600ABCD1234",
  "amount": 500.00,
  "description": "ATM Withdrawal"
}
```

**Response (200 OK):**
```json
{
  "status": 200,
  "message": "Amount debited successfully",
  "data": {
    "id": 1,
    "accountNumber": "ACC1707210600ABCD1234",
    "customerId": 1001,
    "accountType": "CHECKING",
    "balance": 4500.00,
    "currency": "USD",
    "status": "ACTIVE",
    "createdAt": "2026-02-06T21:55:00",
    "updatedAt": "2026-02-06T21:56:00"
  },
  "timestamp": 1707210900000
}
```

**Validation:**
- Account must exist
- Account must not be FROZEN
- Sufficient balance required

---

### 5. Credit Account
**HTTP Method:** `POST`
**Endpoint:** `/api/v1/accounts/credit`
**Description:** Credit funds to an account

**Request Body:**
```json
{
  "accountNumber": "ACC1707210600ABCD1234",
  "amount": 1000.00,
  "description": "Direct Deposit"
}
```

**Response (200 OK):**
```json
{
  "status": 200,
  "message": "Amount credited successfully",
  "data": {
    "id": 1,
    "accountNumber": "ACC1707210600ABCD1234",
    "customerId": 1001,
    "accountType": "CHECKING",
    "balance": 5500.00,
    "currency": "USD",
    "status": "ACTIVE",
    "createdAt": "2026-02-06T21:55:00",
    "updatedAt": "2026-02-06T21:56:30"
  },
  "timestamp": 1707210900000
}
```

**Validation:**
- Account must exist
- Account must not be FROZEN

---

### 6. Freeze Account
**HTTP Method:** `PUT`
**Endpoint:** `/api/v1/accounts/{accountNumber}/freeze`
**Description:** Freeze an account to prevent transactions

**Path Parameters:**
- `accountNumber` (String): The account number to freeze

**Response (200 OK):**
```json
{
  "status": 200,
  "message": "Account frozen successfully",
  "data": {
    "id": 1,
    "accountNumber": "ACC1707210600ABCD1234",
    "customerId": 1001,
    "accountType": "CHECKING",
    "balance": 5500.00,
    "currency": "USD",
    "status": "FROZEN",
    "createdAt": "2026-02-06T21:55:00",
    "updatedAt": "2026-02-06T21:57:00"
  },
  "timestamp": 1707210900000
}
```

---

### 7. Unfreeze Account
**HTTP Method:** `PUT`
**Endpoint:** `/api/v1/accounts/{accountNumber}/unfreeze`
**Description:** Unfreeze an account to restore transaction capability

**Path Parameters:**
- `accountNumber` (String): The account number to unfreeze

**Response (200 OK):**
```json
{
  "status": 200,
  "message": "Account unfrozen successfully",
  "data": {
    "id": 1,
    "accountNumber": "ACC1707210600ABCD1234",
    "customerId": 1001,
    "accountType": "CHECKING",
    "balance": 5500.00,
    "currency": "USD",
    "status": "ACTIVE",
    "createdAt": "2026-02-06T21:55:00",
    "updatedAt": "2026-02-06T21:57:30"
  },
  "timestamp": 1707210900000
}
```

---

## Common Error Responses

### 404 Not Found
```json
{
  "status": 404,
  "message": "Account not found: ACC1234567890",
  "timestamp": 1707210900000
}
```

### 400 Bad Request (Insufficient Balance)
```json
{
  "status": 400,
  "message": "Insufficient balance",
  "timestamp": 1707210900000
}
```

### 400 Bad Request (Frozen Account)
```json
{
  "status": 400,
  "message": "Cannot perform transaction on frozen account",
  "timestamp": 1707210900000
}
```

---

## Account Statuses
- `ACTIVE` - Account is active and can perform transactions
- `FROZEN` - Account is frozen and cannot perform debit/credit operations
- `CLOSED` - Account is closed (reserved for future use)

---

## Files Created/Modified

### Created Files:
1. **Domain Model:** `src/main/java/com/fiserv/uba/account/domain/Account.java`
2. **DTOs:**
   - `src/main/java/com/fiserv/uba/account/dto/AccountDTO.java`
   - `src/main/java/com/fiserv/uba/account/dto/CreateAccountRequest.java`
   - `src/main/java/com/fiserv/uba/account/dto/TransactionRequest.java`
   - `src/main/java/com/fiserv/uba/account/dto/ApiResponse.java`
3. **Repository:** `src/main/java/com/fiserv/uba/account/repository/AccountRepository.java`
4. **Mapper:** `src/main/java/com/fiserv/uba/account/mapper/AccountMapper.java`
5. **Service:** `src/main/java/com/fiserv/uba/account/service/AccountService.java`

### Modified Files:
1. **Controller:** `src/main/java/com/fiserv/uba/account/controller/AccountController.java`
2. **Database Migration:** `src/main/resources/db/migration/V1.0__Initialize_Account_Schema.sql`
3. **Sample Data:** `src/main/resources/db/migration/V1.1__Add_Sample_Data.sql`

---

## Technologies Used
- **Framework:** Spring Boot 2.7.18
- **Language:** Java 8+
- **Database:** MySQL 8.0
- **ORM:** JPA/Hibernate
- **Build:** Maven
- **API Documentation:** This endpoint documentation

---

## Testing the API

### Using cURL:

**Create Account:**
```bash
curl -X POST http://localhost:8081/api/v1/accounts \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 1001,
    "accountType": "CHECKING",
    "initialBalance": 5000.00,
    "currency": "USD"
  }'
```

**Get Account:**
```bash
curl -X GET http://localhost:8081/api/v1/accounts/ACC1707210600ABCD1234 \
  -H "Content-Type: application/json"
```

**Debit Account:**
```bash
curl -X POST http://localhost:8081/api/v1/accounts/debit \
  -H "Content-Type: application/json" \
  -d '{
    "accountNumber": "ACC1707210600ABCD1234",
    "amount": 500.00,
    "description": "ATM Withdrawal"
  }'
```

**Freeze Account:**
```bash
curl -X PUT http://localhost:8081/api/v1/accounts/ACC1707210600ABCD1234/freeze \
  -H "Content-Type: application/json"
```

