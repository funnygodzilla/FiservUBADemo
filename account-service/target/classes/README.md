# Account Service - Resources Documentation

## Overview

This directory contains all configuration files and database migration scripts for the Account Service microservice.

## Directory Structure

```
src/main/resources/
├── application.yml              # Base/default configuration
├── application-dev.yml          # Development profile configuration
├── application-prod.yml         # Production profile configuration
└── db/
    └── migration/
        ├── V1.0__Initialize_Account_Schema.sql
        └── V1.1__Add_Sample_Data.sql
```

## Configuration Files

### application.yml (Base Configuration)

Default Spring Boot configuration with placeholders for environment variables.

**Key Settings:**
- Application name: `account-service`
- Default profile: `dev`
- Database: MySQL 8
- Service discovery: Eureka
- Authentication: JWT
- Monitoring: Actuator & Prometheus

**Environment Variables Supported:**
```
DB_HOST          - Database hostname (default: localhost)
DB_PORT          - Database port (default: 3306)
DB_NAME          - Database name (default: uba_account_db)
DB_USER          - Database username (default: root)
DB_PASSWORD      - Database password (default: root)
JWT_SECRET       - JWT signing secret (required for production)
JWT_EXPIRATION   - Token expiration in ms (default: 86400000 - 24h)
SERVER_PORT      - Server port (default: 8081)
EUREKA_SERVER    - Eureka server URL (default: http://localhost:8761/eureka/)
```

### application-dev.yml (Development Profile)

Optimized for local development with enhanced logging and debugging.

**Key Features:**
- Database URL: `jdbc:mysql://localhost:3306/uba_account_db_dev`
- Hibernate DDL: `create-drop` (recreates schema on each startup)
- SQL logging: `true`
- Eureka: Disabled for local development
- Logging level: DEBUG

**Usage:**
```bash
java -Dspring.profiles.active=dev -jar account-service-1.0.0.jar
```

### application-prod.yml (Production Profile)

Optimized for production environments with security and performance settings.

**Key Features:**
- Database: Requires environment variables (no defaults)
- Hibernate DDL: `validate` (checks schema exists)
- SQL logging: `false`
- Eureka: Enabled with cloud settings
- Logging level: WARN (minimal logging)
- Error details: Hidden from responses (security)
- Connection pool: Optimized for production (50 max connections)
- Log file: `/var/log/account-service/account-service.log`

**Usage:**
```bash
java -Dspring.profiles.active=prod \
  -Dspring.datasource.url=jdbc:mysql://prod-db:3306/uba_account_db \
  -Dspring.datasource.username=$DB_USER \
  -Dspring.datasource.password=$DB_PASSWORD \
  -Djwt.secret=$JWT_SECRET \
  -jar account-service-1.0.0.jar
```

Or with environment variables:
```bash
export DB_HOST=prod-db
export DB_PORT=3306
export DB_NAME=uba_account_db
export DB_USER=prod_user
export DB_PASSWORD=secure_password
export JWT_SECRET=your-256-bit-secret-key
export EUREKA_SERVER=http://eureka-server:8761/eureka/
export SERVER_PORT=8081

java -Dspring.profiles.active=prod -jar account-service-1.0.0.jar
```

## Database Migration Files

Database migrations are managed using Flyway convention (V#.#__Description.sql).

### V1.0__Initialize_Account_Schema.sql

Initial schema creation with the following tables:

#### 1. **accounts**
Core account table with account details.
- Fields: account_number, customer_id, account_type, status, balance, interest_rate, etc.
- Indexes: account_number, customer_id, status, created_at
- Relationships: Referenced by other tables via account_id

#### 2. **account_transactions**
Transaction history for all account operations.
- Fields: transaction_type, amount, description, reference_number, balance_after, status
- Indexes: account_id, transaction_type, status, transaction_date
- Supports: Deposits, Withdrawals, Transfers, Interest Credits

#### 3. **account_limits**
Spending and transaction limits per account.
- Fields: daily_withdrawal_limit, monthly_withdrawal_limit, daily_transfer_limit, monthly_transfer_limit, max_transaction_amount
- Relationship: One-to-One with accounts

#### 4. **account_statements**
Monthly statement records.
- Fields: statement_period_start/end, opening_balance, closing_balance, totals
- Relationship: One-to-Many with accounts

#### 5. **audit_logs**
Audit trail for compliance and security.
- Fields: action, old_value, new_value, ip_address, user_agent, performed_by, performed_at
- No foreign key constraint to prevent accidental deletion

### V1.1__Add_Sample_Data.sql

Sample data for development and testing.

**Includes:**
- 4 sample accounts with different types (CHECKING, SAVINGS, BUSINESS)
- Account limits configuration
- Sample transactions with different types
- Interest credits

**Sample Credentials:**
```
Account 1: ACC-001-2026 (CHECKING) - Balance: $5,000.00
Account 2: ACC-002-2026 (SAVINGS)  - Balance: $10,000.00
Account 3: ACC-003-2026 (CHECKING) - Balance: $2,500.00
Account 4: ACC-004-2026 (BUSINESS) - Balance: $50,000.00
```

## Profiles Activation

### Development Setup

```bash
# Option 1: Using properties
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"

# Option 2: Building and running JAR
mvn clean package
java -Dspring.profiles.active=dev -jar target/account-service-1.0.0.jar

# Option 3: Environment variable
export SPRING_PROFILES_ACTIVE=dev
java -jar target/account-service-1.0.0.jar
```

### Production Setup

```bash
# Using environment variables (recommended)
export SPRING_PROFILES_ACTIVE=prod
export DB_HOST=prod-db-host
export DB_PORT=3306
export DB_NAME=uba_account_prod
export DB_USER=prod_user
export DB_PASSWORD=secure_password
export JWT_SECRET=your-256-bit-secret-key

java -jar target/account-service-1.0.0.jar
```

### Docker Setup

```bash
# Build image
docker build -t account-service:1.0.0 .

# Run with development profile
docker run -d \
  --name account-service-dev \
  -p 8081:8081 \
  -e SPRING_PROFILES_ACTIVE=dev \
  account-service:1.0.0

# Run with production profile
docker run -d \
  --name account-service-prod \
  -p 8081:8081 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DB_HOST=mysql-prod \
  -e DB_PORT=3306 \
  -e DB_NAME=uba_account_prod \
  -e DB_USER=${DB_USER} \
  -e DB_PASSWORD=${DB_PASSWORD} \
  -e JWT_SECRET=${JWT_SECRET} \
  account-service:1.0.0
```

## Database Configuration

### MySQL Setup for Development

```bash
# Create database
mysql -u root -p
CREATE DATABASE uba_account_db_dev;
CREATE DATABASE uba_account_db;

# Verify
SHOW DATABASES;
```

### Connection Pool Settings

**Development (application-dev.yml):**
- Maximum Pool Size: 20
- Minimum Idle: 5
- Connection Timeout: 30s

**Production (application-prod.yml):**
- Maximum Pool Size: 50
- Minimum Idle: 10
- Connection Timeout: 30s

## Logging Configuration

### Development Logging
- **Level:** DEBUG
- **Console Pattern:** Includes timestamp, thread, level, logger name, message
- **Location:** Console only

### Production Logging
- **Level:** WARN
- **File Location:** `/var/log/account-service/account-service.log`
- **Max Size:** 50MB per file
- **Max History:** 90 days
- **Total Capacity:** 10GB
- **Pattern:** Full details with timestamp, thread, level, logger

## Actuator Endpoints

### Development (application-dev.yml)
Exposes: `health`, `info`, `metrics`, `prometheus`, `env`

### Production (application-prod.yml)
Exposes: `health`, `metrics`, `prometheus`

**Access:**
```bash
# Health check
curl http://localhost:8081/api/v1/actuator/health

# Metrics
curl http://localhost:8081/api/v1/actuator/metrics

# Prometheus metrics
curl http://localhost:8081/api/v1/actuator/prometheus
```

## Security Notes

1. **JWT Secret**: Must be at least 256 bits (32 characters) in production
2. **Database Credentials**: Always use environment variables in production
3. **Error Messages**: Production hides stack traces and internal error details
4. **SQL Logging**: Disabled in production for security
5. **Actuator**: Limited endpoints exposed in production

## Troubleshooting

### Connection Refused
```
Error: com.mysql.jdbc.exceptions.jdbc4.CommunicationsException
Solution: Ensure MySQL is running on DB_HOST:DB_PORT
```

### Invalid Database
```
Error: Access denied for user
Solution: Check DB_USER and DB_PASSWORD environment variables
```

### Port Already in Use
```
Error: Port 8081 already in use
Solution: Change SERVER_PORT or kill process using port
```

### JWT Validation Errors
```
Error: JWT validation failed
Solution: Ensure JWT_SECRET matches on all instances
```

---

**Last Updated:** February 2026
**Version:** 1.0.0
