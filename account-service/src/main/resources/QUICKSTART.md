# Account Service Configuration Quick Reference

## Directory Structure Created

```
src/main/resources/
├── README.md                                    # Comprehensive documentation
├── application.yml                              # Base configuration
├── application-dev.yml                          # Development profile
├── application-prod.yml                         # Production profile
└── db/
    └── migration/
        ├── V1.0__Initialize_Account_Schema.sql # Database schema
        └── V1.1__Add_Sample_Data.sql            # Sample data
```

## Quick Start

### 1. Development Mode

```bash
# Build the project
mvn clean package

# Run with development profile
java -Dspring.profiles.active=dev -jar target/account-service-1.0.0.jar
```

**Defaults:**
- Database: `jdbc:mysql://localhost:3306/uba_account_db_dev`
- Server Port: `8081`
- Eureka: Disabled
- SQL Logging: Enabled

### 2. Production Mode

```bash
# Set environment variables
export SPRING_PROFILES_ACTIVE=prod
export DB_HOST=your-db-host
export DB_PORT=3306
export DB_NAME=uba_account_prod
export DB_USER=db_user
export DB_PASSWORD=secure_password
export JWT_SECRET=your-256-bit-secret-key

# Run
java -jar target/account-service-1.0.0.jar
```

### 3. Docker Deployment

```bash
# Build image
docker build -t fiserv-uba-account-service:1.0.0 .

# Run container
docker run -d \
  --name account-service \
  -p 8081:8081 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DB_HOST=mysql \
  -e DB_USER=root \
  -e DB_PASSWORD=password \
  -e JWT_SECRET=your-secret-key \
  fiserv-uba-account-service:1.0.0
```

## Configuration Profiles

| Aspect | Development | Production |
|--------|-------------|-----------|
| Database DDL | `create-drop` | `validate` |
| SQL Logging | Enabled | Disabled |
| Eureka | Disabled | Enabled |
| Log Level | DEBUG | WARN |
| Error Details | Full | Hidden |
| Pool Size | 20 max / 5 min | 50 max / 10 min |
| Log File | Console | `/var/log/account-service/account-service.log` |

## Database Tables

1. **accounts** - Core account information
2. **account_transactions** - Transaction history
3. **account_limits** - Transaction and withdrawal limits
4. **account_statements** - Monthly statements
5. **audit_logs** - Compliance and audit trail

## Sample Accounts (Dev)

```
ACC-001-2026: Checking, $5,000.00
ACC-002-2026: Savings, $10,000.00
ACC-003-2026: Checking, $2,500.00
ACC-004-2026: Business, $50,000.00
```

## Key Endpoints

```
Health Check:  http://localhost:8081/api/v1/actuator/health
Metrics:       http://localhost:8081/api/v1/actuator/metrics
Prometheus:    http://localhost:8081/api/v1/actuator/prometheus
```

## Environment Variables Reference

```yaml
DB_HOST                           # Database host (default: localhost)
DB_PORT                           # Database port (default: 3306)
DB_NAME                           # Database name (default: uba_account_db)
DB_USER                           # Database user (default: root)
DB_PASSWORD                       # Database password (default: root)
JWT_SECRET                        # JWT secret key (REQUIRED in production)
JWT_EXPIRATION                    # Token expiration in ms (default: 86400000)
SERVER_PORT                       # Server port (default: 8081)
EUREKA_SERVER                     # Eureka URL (default: http://localhost:8761/eureka/)
SPRING_PROFILES_ACTIVE            # Active profile: dev or prod
```

## Troubleshooting

### Port 8081 Already in Use
```bash
# Change port
java -Dserver.port=8082 -jar account-service-1.0.0.jar
```

### Database Connection Error
```bash
# Verify MySQL is running
mysql -u root -p

# Check connection string in logs
```

### JWT Validation Failed
```bash
# Ensure JWT_SECRET is set and consistent across all instances
export JWT_SECRET=$(openssl rand -base64 32)
```

### Eureka Registration Failed
```bash
# In dev mode, Eureka is disabled (expected)
# In prod mode, ensure EUREKA_SERVER points to running server
```

---

For detailed documentation, see `README.md` in this directory.
