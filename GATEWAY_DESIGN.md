# Spring Boot API Gateway Design (External Banking Security Platform)

## 📌 Overview
This document describes a production-grade, **reactive** API Gateway pattern for integrating with an external banking security platform. The implementation is **non-blocking**, uses **Spring WebFlux WebClient**, and includes **token orchestration**, **header propagation**, **centralized configuration**, **logging**, and **error mapping**.

---

## ✅ Project Structure
```
src/main/java/com/fiserv/uba/gateway/
├── client/
│   ├── BaseGatewayClient.java       # Common WebClient logic + error mapping
│   └── BankingGatewayClient.java    # External platform integration
├── config/
│   ├── GatewayProperties.java       # Externalized config
│   └── WebClientConfig.java         # WebClient builder + tracing
├── controller/
│   └── GatewayController.java       # API Gateway endpoints
├── dto/
│   ├── ApplicationTokenRequest.java
│   ├── ApplicationTokenResponse.java
│   ├── LoginRequest.java
│   ├── LoginResponse.java
│   ├── PasswordResetRequest.java
│   ├── PasswordResetResponse.java
│   ├── ResponseDTO.java
│   ├── UserCreateRequest.java
│   └── UserResponse.java
├── exception/
│   ├── GatewayClientException.java
│   └── GatewayServerException.java
├── service/
│   ├── GatewayService.java          # Token orchestration + orchestration logic
│   └── TokenService.java            # App token acquisition + caching
└── util/
    ├── HeaderUtils.java             # Authorization/session header utilities
    └── LogUtils.java                # WebClient logging filters
```

---

## ⚙️ Configuration (application.yml)
```yaml
gateway:
  base-url: https://security.examplebank.com
  client-id: client-id
  client-secret: client-secret
  scope: gateway.read gateway.write
  otp-validation-enabled: true
  default-otp: 123456
  connect-timeout: 5s
  read-timeout: 20s
  retry:
    max-attempts: 3
    backoff: 300ms
  endpoints:
    application-token: /oauth2/token
    login: /authenticate
    logout: /logoutUser
    verify-token: /validateToken
    keep-alive: /keepAlive
    request-otp: /authenticate/otp
    validate-otp: /authenticate/otp/validate
    reset-password: /password
    change-password: /password/change
    create-user: /users
    update-user: /users/{userId}
    find-user: /users/search
```

---

## 🔑 Key Configuration Classes
### ✅ WebClientConfig
- Centralized **WebClient.Builder**
- Reactor Netty timeouts
- Logging filters
- Micrometer Observation support

### ✅ GatewayProperties
- All external endpoints, credentials, and timeouts are **externalized**
- Supports multi-environment deployments

---

## 🔁 Base Gateway Client (Reusable, Non-Blocking)
The **BaseGatewayClient** centralizes `GET/POST/PUT` request patterns and handles:
- **JSON serialization**
- **4xx/5xx mapping to custom exceptions**
- Consistent **header injection**

---

## 🔐 Token Orchestration (TokenService)
- Retrieves app token via client credentials
- **Caches token** with TTL to avoid repeated calls
- `flatMap` chaining used when token must be obtained before external calls

---

## ✅ Example Endpoints (Login, OTP, Create User, Reset Password)
### 1. Login
- Obtain app token → call login endpoint → capture session token from headers

### 2. Request/Validate OTP
- Obtain app token → call request OTP endpoint → validate OTP with session ID

### 3. Create User
- Obtain app token → forward session token header → call create user endpoint

### 4. Reset Password
- Obtain app token → call reset password endpoint

---

## 🧠 Header Injection Strategy
- `Authorization: Bearer <appToken>`
- `X-FISV-SESSION: <sessionToken>` for authenticated user flows
- `X-Trace-Id` and `X-Span-Id` propagated from Micrometer Tracer
- Centralized in `HeaderUtils` and `TraceHeaderFilter` to reduce duplication

---

## ⚠️ Error Mapping
- **4xx → GatewayClientException**
- **5xx → GatewayServerException**
- Mapping enforced in BaseGatewayClient and login exchange handler

---

## ✅ Best Practices & Refactoring Ideas
1. **Token caching** with external store (Redis) for multi-instance gateways.
2. **Circuit breaker** (Resilience4j) around external calls.
3. **Retry policy** applied to idempotent requests only.
4. **Tracing** via Micrometer/Brave + Zipkin or OpenTelemetry backend.
5. **Correlation ID propagation** in request headers.
6. **Typed error models** instead of plain strings.
7. **Contract tests** for external API integration.

---

## ✅ Summary
This gateway design delivers:
- Fully reactive I/O
- Centralized WebClient management
- Clean separation of layers
- Extensible configuration and error handling
- Production-ready resilience patterns
