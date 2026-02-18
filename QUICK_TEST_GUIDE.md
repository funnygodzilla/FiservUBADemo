# Quick Reference - Account Service API Testing Guide

## ⚡ Quick Start

### 1. Start the Application
```bash
cd D:\Projects\FiservUBADemo
mvn spring-boot:run
```

### 2. Verify Application is Running
```bash
curl http://localhost:8081/actuator/health
```

Expected response:
```json
{"status":"UP"}
```

---

## 🧪 Test All 7 Endpoints

### Endpoint 1: Create Account (POST)
```bash
curl -X POST http://localhost:8081/api/v1/accounts \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 2001,
    "accountType": "CHECKING",
    "initialBalance": 8000.00,
    "currency": "USD"
  }'
```

**Expected Response:**
```json
{
  "status": 201,
  "message": "Account created successfully",
  "data": {
    "id": 5,
    "accountNumber": "ACC1707210630XXXX1234",
    "customerId": 2001,
    "accountType": "CHECKING",
    "balance": 8000.00,
    "currency": "USD",
    "status": "ACTIVE",
    "createdAt": "2026-02-06T21:57:00",
    "updatedAt": "2026-02-06T21:57:00"
  },
  "timestamp": 1707210900000
}
```

Save the `accountNumber` for the next tests.

---

### Endpoint 2: Get Account (GET)
```bash
curl -X GET http://localhost:8081/api/v1/accounts/ACC1707210600ABCD1234
```

**Expected Response:**
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

### Endpoint 3: Get Customer Accounts (GET)
```bash
curl -X GET http://localhost:8081/api/v1/accounts/customer/1001
```

**Expected Response:**
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

### Endpoint 4: Debit Account (POST)
```bash
curl -X POST http://localhost:8081/api/v1/accounts/debit \
  -H "Content-Type: application/json" \
  -d '{
    "accountNumber": "ACC1707210600ABCD1234",
    "amount": 500.00,
    "description": "ATM Withdrawal"
  }'
```

**Expected Response:**
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
    "updatedAt": "2026-02-06T21:57:30"
  },
  "timestamp": 1707210900000
}
```

**Test Case:** Try debiting more than available balance
```bash
curl -X POST http://localhost:8081/api/v1/accounts/debit \
  -H "Content-Type: application/json" \
  -d '{
    "accountNumber": "ACC1707210600ABCD1234",
    "amount": 10000.00,
    "description": "Large withdrawal"
  }'
```

**Expected Error Response:**
```json
{
  "status": 400,
  "message": "Insufficient balance",
  "timestamp": 1707210900000
}
```

---

### Endpoint 5: Credit Account (POST)
```bash
curl -X POST http://localhost:8081/api/v1/accounts/credit \
  -H "Content-Type: application/json" \
  -d '{
    "accountNumber": "ACC1707210600ABCD1234",
    "amount": 1500.00,
    "description": "Salary Deposit"
  }'
```

**Expected Response:**
```json
{
  "status": 200,
  "message": "Amount credited successfully",
  "data": {
    "id": 1,
    "accountNumber": "ACC1707210600ABCD1234",
    "customerId": 1001,
    "accountType": "CHECKING",
    "balance": 6000.00,
    "currency": "USD",
    "status": "ACTIVE",
    "createdAt": "2026-02-06T21:55:00",
    "updatedAt": "2026-02-06T21:57:45"
  },
  "timestamp": 1707210900000
}
```

---

### Endpoint 6: Freeze Account (PUT)
```bash
curl -X PUT http://localhost:8081/api/v1/accounts/ACC1707210600ABCD1234/freeze \
  -H "Content-Type: application/json"
```

**Expected Response:**
```json
{
  "status": 200,
  "message": "Account frozen successfully",
  "data": {
    "id": 1,
    "accountNumber": "ACC1707210600ABCD1234",
    "customerId": 1001,
    "accountType": "CHECKING",
    "balance": 6000.00,
    "currency": "USD",
    "status": "FROZEN",
    "createdAt": "2026-02-06T21:55:00",
    "updatedAt": "2026-02-06T21:58:00"
  },
  "timestamp": 1707210900000
}
```

**Test Case:** Try to debit frozen account
```bash
curl -X POST http://localhost:8081/api/v1/accounts/debit \
  -H "Content-Type: application/json" \
  -d '{
    "accountNumber": "ACC1707210600ABCD1234",
    "amount": 100.00,
    "description": "Test debit"
  }'
```

**Expected Error Response:**
```json
{
  "status": 400,
  "message": "Cannot perform transaction on frozen account",
  "timestamp": 1707210900000
}
```

---

### Endpoint 7: Unfreeze Account (PUT)
```bash
curl -X PUT http://localhost:8081/api/v1/accounts/ACC1707210600ABCD1234/unfreeze \
  -H "Content-Type: application/json"
```

**Expected Response:**
```json
{
  "status": 200,
  "message": "Account unfrozen successfully",
  "data": {
    "id": 1,
    "accountNumber": "ACC1707210600ABCD1234",
    "customerId": 1001,
    "accountType": "CHECKING",
    "balance": 6000.00,
    "currency": "USD",
    "status": "ACTIVE",
    "createdAt": "2026-02-06T21:55:00",
    "updatedAt": "2026-02-06T21:58:15"
  },
  "timestamp": 1707210900000
}
```

---

## 🔗 Available Test Accounts

The system comes pre-loaded with these test accounts:

```
Account: ACC1707210600ABCD1234
Customer: 1001
Type: CHECKING
Balance: $5,000.00

Account: ACC1707210601EFGH5678
Customer: 1002
Type: SAVINGS
Balance: $10,000.00

Account: ACC1707210602IJKL9012
Customer: 1003
Type: CHECKING
Balance: $2,500.00

Account: ACC1707210603MNOP3456
Customer: 1001
Type: BUSINESS
Balance: $50,000.00
```

---

## ✅ Test Checklist

- [ ] Application starts successfully on port 8081
- [ ] Health check endpoint responds with UP
- [ ] Create Account endpoint returns 201 with account number
- [ ] Get Account endpoint returns account details
- [ ] Get Customer Accounts returns list of accounts
- [ ] Debit Account endpoint reduces balance
- [ ] Credit Account endpoint increases balance
- [ ] Debit rejected when balance insufficient
- [ ] Debit rejected when account frozen
- [ ] Credit rejected when account frozen
- [ ] Freeze Account changes status to FROZEN
- [ ] Unfreeze Account changes status to ACTIVE

---

## 🚨 Common Issues & Solutions

### Issue: Port 8081 Already in Use
```bash
# Kill the process using port 8081
Get-Process | Where-Object {$_.ProcessName -eq "java"} | Stop-Process -Force
```

### Issue: MySQL Connection Refused
```bash
# Verify MySQL is running
mysql -u root -p

# Check if database exists
SHOW DATABASES;

# Create database if needed
CREATE DATABASE uba_account_db_dev;
```

### Issue: Invalid Credentials
```bash
# Update password in application-dev.yml if needed
# Password should be: Mayur@143
```

### Issue: Flyway Migration Error
```bash
# Clear Flyway schema history if needed
TRUNCATE TABLE uba_account_db_dev.flyway_schema_history;
```

---

## 📊 Response Time Expectations

- Create Account: ~50-100ms
- Get Account: ~20-50ms
- List Accounts: ~30-80ms
- Debit/Credit: ~50-150ms
- Freeze/Unfreeze: ~30-100ms

---

## 🔒 Error Codes Reference

| HTTP Status | Message | Cause |
|---|---|---|
| 201 | Account created successfully | Account created |
| 200 | Operation successful | Any successful GET, PUT, POST (except create) |
| 400 | Insufficient balance | Debit amount > available balance |
| 400 | Cannot perform transaction on frozen account | Debit/Credit on frozen account |
| 404 | Account not found | Account number doesn't exist |
| 500 | Internal server error | Server error |

---

## 💾 API Response Structure

All responses include:
- `status` - HTTP status code
- `message` - Human-readable message
- `data` - Response data (AccountDTO, List, or null)
- `timestamp` - Unix timestamp in milliseconds

---

## 🔄 Recommended Test Sequence

1. **Health Check**
   - Verify application is running

2. **Create Test Account**
   - Creates new account for testing
   - Save account number

3. **Retrieve Account**
   - Verify account was created
   - Check initial balance

4. **Credit Account**
   - Add funds to test account
   - Verify new balance

5. **Debit Account**
   - Withdraw funds from test account
   - Verify new balance

6. **Test Validations**
   - Debit more than balance (should fail)
   - Freeze account
   - Try debit on frozen account (should fail)

7. **Unfreeze Account**
   - Restore transaction capability
   - Verify debit works again

8. **Get Customer Accounts**
   - List all accounts for customer
   - Verify test account is included

---

## 📖 For More Information

- **Full API Documentation:** See `API_ENDPOINTS.md`
- **Implementation Details:** See `IMPLEMENTATION_SUMMARY.md`
- **Complete Guide:** See `COMPLETE_IMPLEMENTATION.md`

---

**Happy Testing! 🚀**

