# BA Artifact Pack — U.S. Banking Teller Platform

## 1) Capability Map

### Epic 1: Teller Transaction Processing & Cash Operations

**Business Objective**
- Enable branch/teller staff to execute compliant, controlled, and auditable cash operations with real-time drawer and branch context.

**Capabilities**
1. **Drawer Session Management**
   - Auto-drawer assignment handling (single drawer)
   - Manual drawer selection (multi drawer)
   - Drawer context token exchange
2. **Cash Transactions**
   - Cash deposit
   - Cash withdrawal
   - Cash transfer between internal cashboxes/drawers
3. **Cashbox Lifecycle**
   - Open cashbox (start-of-day)
   - Mid-day adjustments
   - Close and reconcile cashbox (end-of-day)
4. **Approval & Controls**
   - Dual control for high-value transactions
   - Supervisor override with reason capture
5. **Exception Handling**
   - Reversal and correction flows
   - Retry-safe idempotent transaction submission

**Primary Stakeholders**
- Teller
- Branch Operations Manager
- Cash Operations Team
- Service Desk / L2 Support

**Core Systems**
- API Gateway
- User Management Service
- ESF Service
- Integrated Teller Adapter
- UBA DB / USER DB / Redis

---

### Epic 2: Regulatory Compliance, Risk, and Audit Controls

**Business Objective**
- Meet U.S. regulatory and internal control requirements for cash operations and identity-scoped teller activity.

**Capabilities**
1. **Audit & Traceability**
   - Immutable audit events for authentication, drawer selection, token exchange, cashbox and transaction operations
   - Correlation ID tracing across gateway and downstream services
2. **BSA/AML Rule Hooks**
   - Threshold-based alerts
   - Suspicious activity pattern trigger hooks
3. **OFAC / Watchlist Screening Hooks**
   - Pre-transaction screening integration points for relevant transaction types
4. **Role & Entitlement Governance**
   - Effective-dated role and permission checks
   - Segregation-of-duties enforcement
5. **Compliance Reporting**
   - Daily exception reports
   - Teller action and override reports
   - Audit evidence extraction support

**Primary Stakeholders**
- Compliance Officer
- BSA/AML Team
- Internal Audit
- InfoSec / IAM
- Regulators (exam support)

**Core Systems**
- API Gateway
- User Management Service
- Teller Config Service
- Audit/Reporting capability (new)

---

## 2) BRD-Style Requirements

## Epic 1 BRD — Teller Transaction Processing & Cash Operations

### Problem Statement
Current scope supports drawer context enrichment and cashbox detail read flow, but does not provide full teller transaction lifecycle and controls needed for production banking operations.

### In Scope
- Transaction APIs for cash in/out/transfer
- Cashbox open/close/reconcile workflow
- Approval path for threshold transactions
- Idempotent transaction submission and replay safety
- Error handling and meaningful operator responses

### Out of Scope (Phase 1)
- External payments rails (ACH/Wire/RTP)
- Customer onboarding/KYC
- Fraud model scoring engine (advanced ML)

### Functional Requirements
- **FR1:** System shall create cash transactions with drawer-scoped context from enriched JWT claims.
- **FR2:** System shall enforce branch/drawer/token alignment before processing any transaction.
- **FR3:** System shall require supervisor approval for configured thresholds and restricted operations.
- **FR4:** System shall support transaction reversal with mandatory reason codes.
- **FR5:** System shall support start-of-day cashbox open and end-of-day reconcile/close states.
- **FR6:** System shall enforce idempotency keys for all mutating teller transaction APIs.
- **FR7:** System shall publish transaction lifecycle events for downstream reporting/audit.

### Non-Functional Requirements
- **NFR1:** 95th percentile response time for transaction submission <= 500ms (excluding external dependencies).
- **NFR2:** 99.9% monthly availability for gateway and transaction APIs.
- **NFR3:** All mutating operations logged with actor, branch, drawer, timestamp, correlation ID.
- **NFR4:** Zero-trust service-to-service communication (mTLS in target state).

### Data Requirements
- UBA DB tables for transaction state, cashbox state, approvals, reversals.
- Event payload schema versioning for transaction events.

### Assumptions
- Enriched JWT is present after drawer select flow.
- Redis session remains the authoritative active teller session cache.

### Dependencies
- Teller Config thresholds and role permissions.
- User Management role resolution quality.

### Risks
- Inconsistent role/permission mappings across branches.
- Manual override abuse without robust audit policy.

### Success Metrics
- >= 98% straight-through processing for non-exception transactions.
- < 1% reconciliation mismatch at end-of-day.

---

## Epic 2 BRD — Regulatory Compliance, Risk, and Audit Controls

### Problem Statement
Without comprehensive compliance controls, the platform is not ready for U.S. bank production and regulatory examination.

### In Scope
- Immutable audit log framework
- Compliance event schema and retention model
- BSA/AML and OFAC integration points (decision hooks)
- Exception reporting and evidence extraction

### Out of Scope (Phase 1)
- Full case management UI for AML investigations
- Full sanctions engine implementation (only integration hooks)

### Functional Requirements
- **FR1:** System shall capture an immutable audit record for authentication, drawer selection, token exchange, and transaction operations.
- **FR2:** System shall generate compliance alerts when configured thresholds are breached.
- **FR3:** System shall block/hold transaction progression when OFAC/BSA hooks return a hard stop.
- **FR4:** System shall support retention rules and legal hold tags on audit records.
- **FR5:** System shall expose compliance report APIs for date range, branch, teller, and event types.

### Non-Functional Requirements
- **NFR1:** Audit write path must be non-blocking for low-risk events and fail-safe for critical events.
- **NFR2:** All audit and compliance APIs must support trace IDs and deterministic pagination.
- **NFR3:** Data-at-rest encryption for compliance and audit stores.

### Data Requirements
- Audit event schema with: actor, action, resource, before/after snapshot (where applicable), decision, rationale, correlation ID.
- Alert schema with status lifecycle (new, triaged, closed).

### Assumptions
- Centralized logging/monitoring stack is available.
- IAM source of truth exists for role provenance.

### Dependencies
- Role configuration data from Teller Config service.
- Transaction lifecycle events from transaction processing epic.

### Risks
- False-positive alert noise without calibrated thresholds.
- Missing event consistency if downstream services do not propagate correlation IDs.

### Success Metrics
- 100% coverage for critical action audit events.
- < 5 min compliance alert visibility from trigger time.

---

## 3) API Acceptance Criteria (Given / When / Then)

> Note: These criteria are intentionally API-first for automation and UAT readiness.

## Epic 1 — Teller Transaction Processing & Cash Operations

### AC1 — Create Cash Withdrawal
- **Given** a teller has a valid enriched JWT with `sub`, `branchId`, `drawerId`, `itUserId`, and active Redis session
- **And** request includes a unique `Idempotency-Key`
- **When** client calls `POST /api/transactions/withdrawal`
- **Then** API returns `201 Created` with transaction reference, status, and cashbox post-balance
- **And** transaction event is published once
- **And** replay with same idempotency key returns original response without duplicate posting

### AC2 — Threshold Approval Required
- **Given** transaction amount exceeds role/branch threshold
- **When** client calls `POST /api/transactions/withdrawal`
- **Then** API returns `202 Accepted` with `PENDING_APPROVAL`
- **And** approval task is generated for supervisor queue
- **And** no ledger-finalization occurs until approval decision is received

### AC3 — Supervisor Approves Pending Transaction
- **Given** a pending transaction exists and supervisor has permission
- **When** supervisor calls `POST /api/transactions/{txnRef}/approve`
- **Then** API returns `200 OK` and transaction status becomes `APPROVED_POSTED`
- **And** audit log includes approver identity and decision reason

### AC4 — Cashbox Reconciliation at Day End
- **Given** open drawer cashbox session with recorded transactions
- **When** teller calls `POST /api/cashbox/reconcile`
- **Then** system returns expected vs counted amount and variance
- **And** requires supervisor sign-off if variance exceeds tolerance

### AC5 — Drawer/Token Mismatch Protection
- **Given** JWT drawer claim does not match active session context
- **When** any mutating transaction API is called
- **Then** API returns `403 Forbidden`
- **And** no downstream IT/ESF mutation is executed

---

## Epic 2 — Regulatory Compliance, Risk, and Audit Controls

### AC1 — Immutable Audit for Drawer Selection
- **Given** a user selects a drawer through gateway flow
- **When** token exchange completes
- **Then** an immutable audit record is persisted with actor, old/new context, timestamp, correlation ID
- **And** record cannot be updated by standard service APIs

### AC2 — BSA/AML Threshold Alert
- **Given** transaction amount crosses configured AML threshold
- **When** transaction request is submitted
- **Then** system creates compliance alert with status `NEW`
- **And** alert is queryable via compliance report API within SLA

### AC3 — OFAC Hard Stop Hook
- **Given** OFAC integration hook returns `BLOCK`
- **When** transaction request is evaluated
- **Then** API returns `422 Unprocessable Entity` with compliance reason code
- **And** transaction is not posted
- **And** audit entry includes screening outcome

### AC4 — Compliance Evidence Export
- **Given** compliance officer requests events by date range, branch, and teller
- **When** calling `GET /api/compliance/audit-events`
- **Then** API returns paginated results with deterministic sort and trace metadata
- **And** export payload contains all required regulator fields

### AC5 — Correlation ID Propagation
- **Given** inbound request contains `X-Correlation-ID`
- **When** gateway routes through user-management, ESF, and integrated-teller
- **Then** all service logs and audit records include the same correlation ID
- **And** missing correlation ID causes gateway to generate one and return it in response header

---

## 4) Suggested Next BA Deliverables

1. Process swimlanes (happy path + exception path) for each epic.
2. Data dictionary for compliance/audit payloads.
3. Traceability matrix mapping FR/AC to API contracts and test cases.
4. UAT scripts for branch pilot rollout (single-drawer and multi-drawer scenarios).

---

## 5) Implementation Update Timestamp

- Artifact revised on: **2026-02-26 20:12:57 UTC**
- Context: roadmap implementation alignment for audit/compliance foundation, teller transaction domain foundation, gateway correlation/rate-limit hardening.
