# Development PostgreSQL image for TodoApp
# This image includes sample data for local development and testing
FROM postgres:15-alpine

# Set environment variables
ENV POSTGRES_DB=tododb
ENV POSTGRES_USER=todouser
ENV POSTGRES_PASSWORD=todopass

# Copy initialization scripts
COPY ./db/init/ /docker-entrypoint-initdb.d/

# Copy sample data (optional - directory exists but may be empty)
COPY ./db/sample-data/ /docker-entrypoint-initdb.d/sample-data/

# Make scripts executable
RUN chmod +x /docker-entrypoint-initdb.d/*.sql

# Expose PostgreSQL port
EXPOSE 5432

# Add healthcheck (faster for development)
HEALTHCHECK --interval=10s --timeout=5s --retries=5 \
  CMD pg_isready -U todouser -d tododb || exit 1

# Development labels for identification
LABEL maintainer="TodoApp Team"
LABEL version="1.0"
LABEL environment="development"
LABEL description="TodoApp PostgreSQL database with sample data for development" 