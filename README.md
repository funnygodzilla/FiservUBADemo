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
