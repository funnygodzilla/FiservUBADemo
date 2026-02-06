# Fiserv UBA Platform (Demo)

Enterprise-grade Unified Banking Application (UBA) demo aligned with **Fiserv-style** standards. Built for **Java 8**, **Spring Boot microservices**, **MySQL**, **Spring Security (JWT)**, and **domain-driven design**.

---

## 1. High-Level Microservices Architecture

```
fiserv-uba-platform
│
├── config-server
├── discovery-server
├── api-gateway
├── auth-service
├── customer-service
├── account-service
├── transaction-service
├── payment-service
├── notification-service
├── reporting-service
├── audit-service
├── common-library
└── docs
```

### Responsibility Breakdown

| Service                | Purpose                          |
| ---------------------- | -------------------------------- |
| `config-server`        | Centralized config (Git-backed)  |
| `discovery-server`     | Service registry (Eureka/Consul) |
| `api-gateway`          | Routing, auth, rate-limit        |
| `auth-service`         | OAuth2/JWT, RBAC                 |
| `customer-service`     | Customer profile & KYC           |
| `account-service`      | CASA, balances                   |
| `transaction-service`  | Ledger & posting                 |
| `payment-service`      | UPI/IMPS/NEFT style flows        |
| `notification-service` | SMS/Email                        |
| `reporting-service`    | Statements, MIS                  |
| `audit-service`        | Regulatory audit logs            |
| `common-library`       | Shared DTOs, utils               |

---

## 2. Standard Structure of Each Microservice

Example: **account-service**

```
account-service
│
├── src/main/java/com/fiserv/uba/account
│   ├── AccountServiceApplication.java
│   │
│   ├── config
│   │   ├── DataSourceConfig.java
│   │   ├── SecurityConfig.java
│   │   └── SwaggerConfig.java
│   │
│   ├── controller
│   │   └── AccountController.java
│   │
│   ├── service
│   │   ├── AccountService.java
│   │   └── impl
│   │       └── AccountServiceImpl.java
│   │
│   ├── repository
│   │   └── AccountRepository.java
│   │
│   ├── domain
│   │   └── Account.java
│   │
│   ├── dto
│   │   ├── AccountRequestDTO.java
│   │   └── AccountResponseDTO.java
│   │
│   ├── mapper
│   │   └── AccountMapper.java
│   │
│   ├── exception
│   │   ├── BusinessException.java
│   │   ├── GlobalExceptionHandler.java
│   │   └── ErrorResponse.java
│   │
│   ├── client
│   │   └── CustomerServiceClient.java
│   │
│   └── util
│       └── AccountUtils.java
│
├── src/main/resources
│   ├── application.yml
│   ├── application-dev.yml
│   ├── application-prod.yml
│   └── db/migration (Flyway)
│
├── Dockerfile
├── pom.xml
└── README.md
```

---

## 3. Database Layer (MySQL – Banking Style)

### Table Pattern (Example)

```
ACCOUNT
- account_id (PK)
- customer_id
- account_type
- balance
- status
- created_date
- updated_date
```

### Practices

- ✔ Flyway / Liquibase
- ✔ No cascade delete
- ✔ Soft delete via status
- ✔ Separate schema per service
- ✔ No cross-service joins

---

## 4. Common Library (Very Important)

```
common-library
│
├── dto
├── constants
├── enums
├── security
│   └── JwtUtils.java
├── exception
├── util
└── pom.xml
```

Used by:

- All microservices
- Prevents duplication
- Ensures consistency

---

## 5. Security (Fiserv-Grade)

### auth-service

- OAuth2 + JWT
- Roles:
  - `ROLE_CUSTOMER`
  - `ROLE_TELLER`
  - `ROLE_MANAGER`
  - `ROLE_ADMIN`

### api-gateway

- Token validation
- Rate limiting
- IP whitelisting

---

## 6. Transaction Handling (Critical for Banking)

- Saga Pattern
- Idempotency keys
- No distributed DB transactions
- Event-based rollback

Example flow:

```
Transaction → Account → Notification
```

---

## 7. Logging & Audit (Regulatory Requirement)

- Centralized logging (ELK)
- Audit-service for:
  - Login
  - Fund transfer
  - Balance inquiry
- Immutable audit records

---

## 8. Why This Structure Fits Fiserv / UBA

- ✔ Clear domain separation
- ✔ Compliant with banking regulations
- ✔ Easy BGV / audit explanation
- ✔ Interview-ready architecture
- ✔ Scales horizontally
- ✔ Secure by default

---

## 9. Interview Explanation (One-liner)

> “We designed a domain-driven microservices architecture where each service owns its database, communication is secured via JWT at the gateway, transactions are managed using Saga patterns, and audit/compliance is handled by a dedicated service.”

---

## Next (Recommended)

- 🔐 JWT + Spring Security code
- 🔄 Saga implementation example
- 🌐 API Gateway routing config
- 🧪 Sample transaction flow (end-to-end)
- 🎯 How to explain this in **Fiserv interviews**
