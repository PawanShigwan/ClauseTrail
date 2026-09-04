# ─── Stage 1: Build ───────────────────────────────────────────────────────────
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder

WORKDIR /app

# Copy pom.xml first and download dependencies (layer-caching optimization)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source and build the fat JAR (skip tests – they run in CI)
COPY src ./src
RUN mvn clean package -DskipTests -B

# ─── Stage 2: Runtime ─────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine AS runtime

# Security: run as non-root user
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

WORKDIR /app

# Copy the fat JAR from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Render dynamically assigns a port via $PORT; default to 8080 for local Docker runs
ENV PORT=8080

EXPOSE $PORT

# JVM tuning for containerised environments (Render free tier ≈ 512 MB RAM):
#   UseContainerSupport  – respect cgroup memory limits instead of host RAM
#   MaxRAMPercentage     – allow JVM to use up to 75 % of available container RAM
#   egd                  – faster SecureRandom seeding on Linux
ENTRYPOINT ["sh", "-c", "java \
  -XX:+UseContainerSupport \
  -XX:MaxRAMPercentage=75.0 \
  -Djava.security.egd=file:/dev/./urandom \
  -Dserver.port=${PORT} \
  -jar app.jar"]
