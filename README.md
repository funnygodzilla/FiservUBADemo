# Fiserv UBA Platform (Enterprise Demo Baseline)

Enterprise-oriented multi-service implementation for teller drawer context enrichment and downstream cashbox access.

## Tech Stack
- Java 17
- Spring Boot 3.x
- Spring Cloud Gateway (WebFlux)
- Spring Security + JWT
- Spring Data JPA (PostgreSQL)
- Spring Data Redis
- OpenFeign
- Kafka dependencies (event-ready)
- Liquibase
- Docker-ready modules

## Implemented Services
- `api-gateway` (reactive edge service)
- `user-management-service`
- `teller-config-service`
- `esf-service`
- `integrated-teller-service` (downstream mock)

## Contracted APIs
### API Gateway
- `GET /api/drawers`
- `POST /api/drawer/select/{drawerId}`
- `GET /cashbox/details`

### User Management Service
- `GET /users/{sub}/drawers`
- `POST /users/{sub}/drawer/select/{drawerId}`

### Teller Config Service
- `GET /config/roles/{roleId}`
- `GET /config/branches/{branchId}`

### ESF + Integrated Teller
- `GET /cashbox/details`

## Enterprise Flow Notes
1. Gateway validates JWT before routing.
2. `GET /api/drawers` retrieves drawers from user-management.
3. Single drawer assignment triggers immediate selection and token exchange.
4. Gateway returns enriched token in `X-New-JWT`.
5. Session context is saved in Redis as `session:{userId}:{branchId}:{drawerId}`.
6. `GET /cashbox/details` requires enriched JWT and active Redis session.
7. Drawer-selection failures are returned as 4xx and do not replace existing token.

## Databases
### USER DB (Liquibase)
- `users`
- `drawers`
- `branches`
- `roles`
- `user_drawer_mapping`
- `user_role_mapping`

### UBA DB (Liquibase)
- `transaction_log`
- `cashbox_state`

## Build / Validation
```bash
mvn -q -f api-gateway/pom.xml test
mvn -q -f user-management-service/pom.xml -DskipTests compile
mvn -q -f teller-config-service/pom.xml -DskipTests compile
mvn -q -f esf-service/pom.xml -DskipTests compile
mvn -q -f integrated-teller-service/pom.xml -DskipTests compile
```

## Newly Implemented Roadmap Items

- Added **Audit Service** foundation for immutable audit event persistence (`/api/v1/audit-events`) including actor, branch, drawer, correlationId, before/after, timestamp.
- Added **Transaction Service** foundation for teller cash operations:
  - `POST /api/v1/transactions/cash-in`
  - `POST /api/v1/transactions/cash-out`
  - `POST /api/v1/transactions/transfer`
  - `POST /api/v1/transactions/{txnRef}/approve`
  - `POST /api/v1/transactions/{txnRef}/reverse`
  - `POST /api/v1/cashbox/reconcile`
- Implemented **AML threshold hook** and **OFAC hard-stop hook** integration points in transaction domain service.
- Implemented **Gateway Correlation ID** propagation and response header support.
- Added **Gateway rate limiting** global filter and downstream call timeout controls.
- Added **API version aliases** (`/api/v1/...`) for gateway flows.

## Change Log
- Updated on: 2026-02-26 20:12:57 UTC (baseline check)
- Updated on: 2026-02-26 20:12:57 UTC + implementation pass completed in this change set


## Enterprise Expansion (Compliance + Teller Ops + Frontend)

This iteration extends the architecture with enterprise controls and an operations UI:

- **Transaction service enhancements**
  - Cashbox lifecycle APIs: `POST /api/v1/cashbox/open`, `POST /api/v1/cashbox/close`
  - Adjustment API: `POST /api/v1/cashbox/adjustments`
  - Variance dual-approval API: `POST /api/v1/cashbox/variance/approve`
  - Idempotency replay API: `GET /api/v1/idempotency/{idempotencyKey}`
  - Maker-checker guard (approver cannot be initiator) and supervisor enforcement for high-value flows
- **Audit service enhancements**
  - Compliance alert lifecycle APIs:
    - `POST /api/v1/compliance/alerts`
    - `POST /api/v1/compliance/alerts/{alertId}/disposition`
    - `POST /api/v1/compliance/alerts/{alertId}/legal-hold`
    - `GET /api/v1/compliance/reports`
  - OFAC decision persistence + override rationale requirement
  - Legal hold state operations for evidence retention governance
- **Frontend module**
  - Added `ops-portal-ui` Spring Boot web app (port `8090`) with a single-page operations console for teller + compliance workflows.

## Change Log
- Updated on: 2026-02-26 20:37:56 UTC (enterprise implementation + frontend portal)


## Production-Readiness Hardening Added

- Transaction-to-audit now uses durable outbox publishing with retry scheduling (instead of placeholder no-op).
- Compliance decision hooks now generate alert records in audit-service and support configurable OFAC block actors and AML thresholds.
- Centralized entitlement check endpoint is exposed from teller-config and consumed by transaction-service for deny-by-default enforcement.
- Gateway rate limiting now uses Redis counters when available, with in-memory fallback for local environments.

## Local Initial Testing

1. Start local dependencies:
```bash
docker compose -f docker-compose.local.yml up -d
```

2. Compile services:
```bash
mvn -q -f teller-config-service/pom.xml -DskipTests compile
mvn -q -f audit-service/pom.xml -DskipTests compile
mvn -q -f transaction-service/pom.xml -DskipTests compile
mvn -q -f api-gateway/pom.xml test
```

3. Run services locally (separate shells):
```bash
mvn -q -f teller-config-service/pom.xml spring-boot:run
mvn -q -f audit-service/pom.xml spring-boot:run
mvn -q -f transaction-service/pom.xml spring-boot:run
mvn -q -f api-gateway/pom.xml spring-boot:run
```

4. Smoke test entitlement + transaction flow:
```bash
curl -X POST http://localhost:8087/api/v1/transactions/cash-out \
  -H 'Content-Type: application/json' \
  -H 'Idempotency-Key: test-key-1' \
  -d '{"branchId":"BR-1001","drawerId":"DR-1001","initiatedBy":"teller01","amount":12000,"reasonCode":"TEST"}'
```

## Change Log
- Updated on: 2026-03-02 04:48:04 UTC (production-hardening pass + local-test bootstrap)
