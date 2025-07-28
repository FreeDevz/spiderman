# Custom PostgreSQL image with schema initialization
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

# Add healthcheck
HEALTHCHECK --interval=10s --timeout=5s --retries=5 \
  CMD pg_isready -U todouser -d tododb || exit 1

# Optional: Add custom postgresql.conf for performance tuning
# COPY postgresql.conf /etc/postgresql/postgresql.conf 