# Development Dockerfile for TodoApp Spring Boot Backend
# This image is optimized for local development and testing

FROM openjdk:21-jdk-slim

# Set working directory
WORKDIR /app

# Install necessary packages for development
RUN apt-get update && apt-get install -y \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Copy Gradle wrapper and build files
COPY gradlew .
COPY gradle/ gradle/
COPY build.gradle.kts .

# Make gradlew executable
RUN chmod +x gradlew

# Download dependencies (this layer will be cached if dependencies don't change)
RUN ./gradlew dependencies --no-daemon

# Copy source code
COPY src/ src/

# Build the application
RUN ./gradlew bootJar -x test --no-daemon

# Development labels
LABEL maintainer="TodoApp Team"
LABEL version="1.0"
LABEL environment="development"
LABEL description="TodoApp Spring Boot backend for development"

# Expose port
EXPOSE 8080

# Health check for development (faster intervals)
HEALTHCHECK --interval=10s --timeout=5s --retries=5 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run the application
CMD ["sh", "-c", "java -jar build/libs/*.jar"] 