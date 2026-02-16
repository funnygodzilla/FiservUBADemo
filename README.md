# FiservUBADemo
# FiservUBADemo - Unified Banking Application

Enterprise-grade Unified Banking Application (UBA) built using Java 8, Spring Boot microservices, MySQL, Spring Security (JWT), API Gateway, and domain-driven design principles.

## Multi-Service Split (Updated)

The repository is now organized as a **multi-module Maven project** with separate deployable services:

- `account-service/` → Account domain APIs, MySQL/Flyway resources, and account Spring Boot app.
- `gateway-service/` → Reactive API Gateway integration for external banking security platform.

Build all modules:
```bash
mvn clean package
```

Build a single module:
```bash
mvn -pl gateway-service -am clean package
mvn -pl account-service -am clean package
```

## Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Technologies](#technologies)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Build & Run](#build--run)
- [Docker Deployment](#docker-deployment)
- [Configuration](#configuration)
- [API Documentation](#api-documentation)
- [Development](#development)

## Overview

FiservUBADemo is a comprehensive microservices-based banking application that provides:

- **Account Management**: Complete account lifecycle management
- **Security**: JWT-based authentication and authorization
- **Scalability**: Cloud-ready microservices architecture
- **Reliability**: Spring Boot best practices and production-ready configurations
- **Integration**: OpenFeign for inter-service communication
- **Monitoring**: Spring Boot Actuator for health checks and metrics

## Architecture

The application follows a microservices architecture pattern with the following components:

```
┌─────────────────────────────────────────────────┐
│           API Gateway / Load Balancer            │
└──────────────────┬──────────────────────────────┘
                   │
       ┌───────────┼───────────┐
       │           │           │
┌──────▼──────┐   │    ┌──────▼──────┐
│   Account   │   │    │   Other     │
│  Service    │   │    │  Services   │
└──────┬──────┘   │    └─────────────┘
       │          │
┌──────▼──────┐   │    ┌──────────────┐
│   MySQL     │   │    │ Eureka/      │
│  Database   │   │    │ Service Disc. │
└─────────────┘   │    └──────────────┘
                  │
           ┌──────▼──────┐
           │   JWT Auth  │
           │   Provider  │
           └─────────────┘
```

## Technologies

- **Java 8**: Core language
- **Spring Boot 2.7.14**: Application framework
- **Spring Cloud 2021.0.8**: Cloud-native features
  - Eureka: Service discovery
  - OpenFeign: Declarative HTTP client
- **Spring Security**: Authentication & Authorization
- **JWT (JJWT 0.11.5)**: Token-based security
- **Spring Data JPA**: Data persistence
- **MySQL 8.0**: Relational database
- **Lombok**: Boilerplate code reduction
- **MapStruct**: Object mapping
- **Maven**: Build tool
- **Docker**: Containerization

## Project Structure

```
FiservUBADemo/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/fiserv/uba/account/
│       │       ├── AccountServiceApplication.java    # Main application class
│       │       ├── controller/                        # REST endpoints
│       │       ├── service/                           # Business logic
│       │       ├── repository/                        # Data access layer
│       │       ├── domain/                            # Entity classes
│       │       ├── dto/                               # Data Transfer Objects
│       │       ├── mapper/                            # Entity to DTO mappers
│       │       ├── exception/                         # Custom exceptions
│       │       ├── config/                            # Configuration classes
│       │       ├── client/                            # Feign clients
│       │       └── util/                              # Utility classes
│       └── resources/
│           └── application.yml                        # Application configuration
├── pom.xml                                             # Maven dependencies
├── Dockerfile                                          # Docker image definition
├── README.md                                           # This file
└── .gitignore                                          # Git ignore rules
```

## Prerequisites

- **Java 8 or higher**: `java -version`
- **Maven 3.6+**: `mvn -version`
- **MySQL 8.0+**: Database server running
- **Docker** (optional): For containerized deployment

## Build & Run

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/FiservUBADemo.git
cd FiservUBADemo
```

### 2. Configure Application

Create `src/main/resources/application.yml`:

```yaml
spring:
  application:
    name: account-service
  datasource:
    url: jdbc:mysql://localhost:3306/uba_account_db
    username: root
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
    properties:
      hibernate:
        format_sql: true
        dialect: org.hibernate.dialect.MySQL8Dialect
  security:
    jwt:
      secret: your-secret-key-min-256-bits-long
      expiration: 86400000  # 24 hours

server:
  port: 8080
  servlet:
    context-path: /api

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
  instance:
    prefer-ip-address: true

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: when-authorized
```

### 3. Build the Application

```bash
mvn clean package
```

### 4. Run the Application

```bash
java -jar target/account-service-1.0.0.jar
```

The application will start on `http://localhost:8080`

## Docker Deployment

### Build Docker Image

```bash
docker build -t fiserv-uba-account-service:latest .
```

### Run Container

```bash
docker run -d \
  --name account-service \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/uba_account_db \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=your_password \
  -e SPRING_SECURITY_JWT_SECRET=your-secret-key \
  fiserv-uba-account-service:latest
```

### Docker Compose (Optional)

For complete stack deployment with MySQL:

```yaml
version: '3.8'
services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: uba_account_db
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql

  account-service:
    build: .
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/uba_account_db
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: root
    depends_on:
      - mysql

volumes:
  mysql_data:
```

## Configuration

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `SPRING_DATASOURCE_URL` | Database URL | `jdbc:mysql://localhost:3306/uba_account_db` |
| `SPRING_DATASOURCE_USERNAME` | Database username | `root` |
| `SPRING_DATASOURCE_PASSWORD` | Database password | Required |
| `SPRING_SECURITY_JWT_SECRET` | JWT signing secret | Required |
| `SERVER_PORT` | Server port | `8080` |
| `EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE` | Eureka server URL | `http://localhost:8761/eureka/` |

### Application Properties

See `src/main/resources/application.yml` for complete configuration options.

## API Documentation

### Health Check

```bash
curl http://localhost:8080/api/actuator/health
```

### Info

```bash
curl http://localhost:8080/api/actuator/info
```

### Metrics

```bash
curl http://localhost:8080/api/actuator/metrics
```

## Development

### Adding Dependencies

Edit `pom.xml` and add your dependency, then run:

```bash
mvn dependency:resolve
```

### Running Tests

```bash
mvn test
```

### Code Quality

```bash
mvn clean verify
```

## Troubleshooting

### Connection Refused

Ensure MySQL is running and accessible at the configured URL.

### Port Already in Use

Change `server.port` in application.yml or use:

```bash
java -Dserver.port=8081 -jar target/account-service-1.0.0.jar
```

### Build Errors

Clear Maven cache:

```bash
mvn clean install -U
```

## License

This project is part of Fiserv's Banking Solutions and is proprietary.

## Support

For issues and questions, contact your development team or create an issue in the repository.

---

**Last Updated**: February 2026
**Version**: 1.0.0
