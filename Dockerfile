# Stage 1: Build
FROM maven:3.8.4-openjdk-11 AS builder
WORKDIR /build
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:11-jdk-alpine

# Metadata
LABEL maintainer="devops-team@company.com"
LABEL version="1.0"

# Environment variables
ENV APP_PORT=8082 \
    SPRING_PROFILES_ACTIVE=prod \
    JAVA_OPTS="-Xms256m -Xmx512m"

# Create app directory
WORKDIR /app

# Copy JAR from builder
COPY --from=builder /build/target/kaddem-*.jar app.jar

# Health check
HEALTHCHECK --interval=30s --timeout=3s \
    CMD wget -q --spider http://localhost:${APP_PORT}/actuator/health || exit 1

# Expose port
EXPOSE ${APP_PORT}

# Entrypoint with exec form
ENTRYPOINT exec java ${JAVA_OPTS} -jar app.jar