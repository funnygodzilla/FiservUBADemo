# -----------------------------
# Stage 1: Build
# -----------------------------
FROM maven:3.8.6-openjdk-8-slim AS builder

WORKDIR /app

# Copy pom.xml and download dependencies (cache optimization)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# -----------------------------
# Stage 2: Runtime
# -----------------------------
FROM openjdk:8-jre-slim

WORKDIR /app

# Install curl (for health check)
RUN apt-get update \
    && apt-get install -y curl \
    && rm -rf /var/lib/apt/lists/*

# Copy jar from builder stage
COPY --from=builder /app/target/account-service*.jar app.jar

# Expose Account Service port
EXPOSE 8081

# JVM options suitable for containers (banking-safe)
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC -XX:+ExitOnOutOfMemoryError"

# Health check (Spring Boot Actuator)
HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8081/actuator/health || exit 1

# Run application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
