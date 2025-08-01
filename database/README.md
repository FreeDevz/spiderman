# TodoApp Database Setup Guide

This guide covers setting up the PostgreSQL database container for both **local development** and **production** deployment using Podman.

## 📋 **Table of Contents**

- [Overview](#overview)
- [Local Development Setup](#local-development-setup)
- [Production Deployment](#production-deployment)
- [Database Schema](#database-schema)
- [Environment Variables](#environment-variables)
- [Troubleshooting](#troubleshooting)
- [Advanced Configuration](#advanced-configuration)

## 🎯 **Overview**

The TodoApp database is built on **PostgreSQL 15-alpine** with:
- **7 core tables** with proper relationships and constraints
- **48 optimized indexes** for query performance
- **Automated triggers** for timestamp management
- **Separate Dockerfiles** for development and production environments
- **Sample data** for development (excluded in production)
- **Health checks** and monitoring support
- **Comprehensive backup and recovery** procedures
- **✅ Successfully deployed and tested** with Podman Desktop

## 🛠️ **Local Development Setup**

### Prerequisites

- **Podman Desktop** installed and running
- **Git** (for repository access)

### Quick Start

```bash
# Navigate to database directory
cd database/

# Build the development container with sample data
podman build -t todoapp-database .
# Note: This uses the default Dockerfile which includes sample data

# Run the database container
podman run -d \
  --name todoapp-db \
  -p 5432:5432 \
  -e POSTGRES_DB=tododb \
  -e POSTGRES_USER=todouser \
  -e POSTGRES_PASSWORD=todopass \
  todoapp-database

# Check if container is running
podman ps | grep todoapp-db

# View initialization logs
podman logs todoapp-db
```

### Using Individual Containers (Recommended)

From the project root directory:

```bash
# Build database image
cd database && podman build -t todoapp-database:latest .

# Create network and run database
cd .. && podman network create todo-network
podman run -d --name todoapp-database --network todo-network -p 5432:5432 \
  -e POSTGRES_DB=tododb -e POSTGRES_USER=todouser -e POSTGRES_PASSWORD=todopass \
  todoapp-database:latest

# Wait for database to initialize, then start backend
sleep 10 && cd backend && podman build -t todoapp-backend:latest .
cd .. && podman run -d --name todoapp-backend --network todo-network -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=docker \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://todoapp-database:5432/tododb \
  -e SPRING_DATASOURCE_USERNAME=todouser -e SPRING_DATASOURCE_PASSWORD=todopass \
  -e JWT_SECRET=defaultSecretForDev todoapp-backend:latest
```

### Using Docker Compose (Alternative)

```bash
# Start database service only
docker-compose up database -d

# Or start all services (when available)
docker-compose up -d

# Stop services
docker-compose down

# View logs
docker-compose logs database
```

### Database Connection

- **Host**: `localhost`
- **Port**: `5432`
- **Database**: `tododb`
- **Username**: `todouser`
- **Password**: `todopass`

### Testing the Connection

```bash
# Connect using psql
podman exec -it todoapp-db psql -U todouser -d tododb

# Or from host (if postgresql-client installed)
psql -h localhost -p 5432 -U todouser -d tododb
```

### Sample Data

The development setup includes comprehensive sample data:

```sql
-- Demo users (password: "password")
demo@example.com        -- Main demo user with full dataset
john.doe@example.com    -- Additional test user
jane.smith@example.com  -- Another test user

-- Sample data includes:
-- • 25+ tasks across different categories
-- • 5 categories with color coding
-- • 8 tags for flexible organization
-- • 4 notifications for testing
-- • User settings and preferences
-- • Dashboard statistics for testing
```

### Development Commands

```bash
# Restart database with fresh data
podman stop todoapp-db && podman rm todoapp-db
podman run -d --name todoapp-db -p 5432:5432 todoapp-database

# Backup development data
podman exec todoapp-db pg_dump -U todouser tododb > backup.sql

# Access database shell
podman exec -it todoapp-db psql -U todouser -d tododb

# View table structure
podman exec todoapp-db psql -U todouser -d tododb -c "\dt"

# Check sample user data
podman exec todoapp-db psql -U todouser -d tododb -c "SELECT name, email FROM users;"

# Check health status
podman exec todoapp-db pg_isready -U todouser -d tododb
```

## 🚀 **Production Deployment**

### Prerequisites

- **Podman** installed and configured
- **Container registry** (optional, for image distribution)

### Step 1: Build Production Image

The production environment uses a separate Dockerfile (`Dockerfile.prod`) that:
- **Excludes sample data** (no `03_sample_data.sql`)
- **Includes production configuration** (`postgresql.prod.conf`)
- **Has optimized health checks** for production monitoring
- **Contains production labels** for container management
- **Uses optimized PostgreSQL settings** for production workloads

### Step 2: Build and Deploy

```bash
# Build production image
podman build -f Dockerfile.prod -t todoapp-database-prod .

# Create network and run production database
podman network create todo-network
podman run -d --name todoapp-database-prod --network todo-network -p 5432:5432 \
  -e POSTGRES_DB=tododb -e POSTGRES_USER=todouser -e POSTGRES_PASSWORD=securepassword \
  -v postgres_data:/var/lib/postgresql/data \
  todoapp-database-prod

# Start backend with production database
podman run -d --name todoapp-backend-prod --network todo-network -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=docker \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://todoapp-database-prod:5432/tododb \
  -e SPRING_DATASOURCE_USERNAME=todouser -e SPRING_DATASOURCE_PASSWORD=securepassword \
  -e JWT_SECRET=your-production-secret todoapp-backend:latest
```

### Step 3: Verify Production Deployment

```bash
# Check container status
podman ps

# Test database connection
podman exec todoapp-database-prod pg_isready -U todouser -d tododb

# Verify schema without sample data
podman exec todoapp-database-prod psql -U todouser -d tododb -c "SELECT COUNT(*) FROM users;"
# Should return 0 for production (no sample data)

# Test health endpoint
curl http://localhost:8080/actuator/health
```

## ✅ **Verified Working Features**

### Database Operations
- ✅ Schema creation with all 7 tables
- ✅ 48 optimized indexes for performance
- ✅ Automated triggers for timestamp management
- ✅ User data isolation and security
- ✅ Health monitoring and diagnostics

### Container Management
- ✅ Podman deployment with individual Dockerfiles
- ✅ Network communication between containers
- ✅ Environment-specific configurations
- ✅ Health checks and monitoring
- ✅ Data persistence and backup support

### Integration Testing
- ✅ Backend application connectivity
- ✅ JPA entity mapping and validation
- ✅ Transaction management
- ✅ Connection pooling with HikariCP
- ✅ Health endpoint integration

## 📊 **Database Schema**

### Tables Overview

| Table | Purpose | Key Features |
|-------|---------|--------------|
| `users` | User accounts | Authentication, email verification, password hashing |
| `categories` | Task organization | Color coding, user-specific, soft delete |
| `tasks` | Main todo items | Status, priority, due dates, bulk operations |
| `tags` | Flexible labeling | Many-to-many with tasks, color coding |
| `task_tags` | Tag relationships | Junction table with optimized indexes |
| `user_settings` | User preferences | Theme, timezone, notifications, privacy |
| `notifications` | In-app messages | Scheduled, metadata support, read status |

### Key Relationships

```sql
-- Users own categories, tasks, tags, settings, and notifications
users (1) ←→ (N) categories
users (1) ←→ (N) tasks
users (1) ←→ (N) tags
users (1) ←→ (1) user_settings
users (1) ←→ (N) notifications

-- Categories can have multiple tasks
categories (1) ←→ (N) tasks

-- Tasks can have multiple tags (many-to-many)
tasks (N) ←→ (N) tags (via task_tags)
```

### Dashboard View

```sql
-- Pre-built aggregation view for quick statistics
SELECT * FROM user_dashboard_stats WHERE user_id = 1;
-- Returns: total_tasks, completed_tasks, pending_tasks, overdue_tasks, etc.
```

### Indexes and Performance

The database includes 48 optimized indexes for:
- **User queries**: Email, username lookups
- **Task filtering**: Status, priority, due date, category
- **Tag operations**: Name-based searches
- **Dashboard queries**: Aggregated statistics
- **Notification delivery**: User and status filtering

## 🔧 **Environment Variables**

### Development

```bash
POSTGRES_DB=tododb
POSTGRES_USER=todouser
POSTGRES_PASSWORD=todopass
POSTGRES_INITDB_ARGS="--encoding=UTF-8 --lc-collate=C --lc-ctype=C"
```

### Production

```bash
POSTGRES_DB=tododb
POSTGRES_USER=todouser
POSTGRES_PASSWORD=<secure-password>
PGDATA=/var/lib/postgresql/data
POSTGRES_INITDB_ARGS="--encoding=UTF-8 --lc-collate=C --lc-ctype=C"
```

### Container-Specific

```bash
# Use environment variables for configuration
POSTGRES_PASSWORD_FILE=/etc/secrets/postgres-password
```

## 🛠️ **Troubleshooting**

### Local Development Issues

#### Container Won't Start

```bash
# Check logs for initialization errors
podman logs todoapp-db

# Common issues:
# 1. Port already in use
podman ps -a | grep 5432
pkill -f postgres  # Kill any local postgres

# 2. Permission issues
podman rm todoapp-db
podman run -d --name todoapp-db -p 5432:5432 todoapp-database

# 3. Image build failures
podman build --no-cache -t todoapp-database .

# 4. Health check failures
podman exec todoapp-db pg_isready -U todouser -d tododb
```

#### Connection Issues

```bash
# Test container networking
podman exec todoapp-db ping 127.0.0.1

# Check PostgreSQL is listening
podman exec todoapp-db netstat -tlnp | grep 5432

# Test authentication
podman exec todoapp-db psql -U todouser -d tododb -c "SELECT 1;"

# Check connection from host
psql -h localhost -p 5432 -U todouser -d tododb -c "SELECT version();"
```

#### Sample Data Issues

```bash
# Verify sample data loaded
podman exec todoapp-db psql -U todouser -d tododb -c "SELECT COUNT(*) FROM users;"

# Reload sample data
podman exec todoapp-db psql -U todouser -d tododb -f /docker-entrypoint-initdb.d/03_sample_data.sql

# Check specific tables
podman exec todoapp-db psql -U todouser -d tododb -c "SELECT COUNT(*) FROM tasks;"
podman exec todoapp-db psql -U todouser -d tododb -c "SELECT COUNT(*) FROM categories;"
```

### Production Issues

#### Container Startup Problems

```bash
# Check container logs
podman logs todoapp-database-prod

# Check container status
podman ps -a

# Common issues:
# 1. Image build errors - verify Dockerfile.prod
# 2. Volume mount issues - check permissions
# 3. Health check failures - verify pg_isready command
# 4. Network connectivity - check todo-network
```

#### Database Performance

```bash
# Check resource usage
podman stats todoapp-database-prod

# Review slow queries
podman exec todoapp-database-prod psql -U todouser -d tododb -c "
SELECT query, calls, total_time, mean_time 
FROM pg_stat_statements 
ORDER BY total_time DESC LIMIT 10;"

# Check connection pool status
podman exec todoapp-database-prod psql -U todouser -d tododb -c "
SELECT count(*) as active_connections 
FROM pg_stat_activity 
WHERE state = 'active';"
```

#### Connection Issues

```bash
# Test from within container network
podman exec todoapp-backend-prod ping todoapp-database-prod

# Check service endpoints
podman network inspect todo-network

# Test health check
podman exec todoapp-database-prod pg_isready -U todouser -d tododb
```

## ⚙️ **Advanced Configuration**

### Custom PostgreSQL Settings

For production workloads, tune PostgreSQL parameters:

```sql
-- Check current settings
SHOW all;

-- Key production settings
ALTER SYSTEM SET shared_buffers = '256MB';
ALTER SYSTEM SET effective_cache_size = '1GB';
ALTER SYSTEM SET random_page_cost = 1.1;
ALTER SYSTEM SET checkpoint_completion_target = 0.9;
ALTER SYSTEM SET wal_buffers = '16MB';
ALTER SYSTEM SET default_statistics_target = 100;

-- Reload configuration
SELECT pg_reload_conf();
```

### Backup and Recovery

#### Development Backup

```bash
# Create backup
podman exec todoapp-db pg_dump -U todouser tododb > backup-$(date +%Y%m%d).sql

# Create compressed backup
podman exec todoapp-db pg_dump -U todouser tododb | gzip > backup-$(date +%Y%m%d).sql.gz

# Restore backup
podman exec -i todoapp-db psql -U todouser -d tododb < backup-20231215.sql

# Restore compressed backup
gunzip -c backup-20231215.sql.gz | podman exec -i todoapp-db psql -U todouser -d tododb
```

#### Production Backup

```bash
# Create backup
podman exec todoapp-database-prod pg_dump -U todouser tododb | gzip > backup-$(date +%Y%m%d).sql.gz

# Automated backup with retention
# Add to crontab: 0 2 * * * /path/to/backup-script.sh

# Restore production backup
gunzip -c backup-20231215.sql.gz | podman exec -i todoapp-database-prod psql -U todouser -d tododb
```

### Monitoring and Metrics

#### Enable Query Statistics

```sql
-- Enable pg_stat_statements extension
CREATE EXTENSION IF NOT EXISTS pg_stat_statements;

-- View query performance
SELECT query, calls, total_time, mean_time 
FROM pg_stat_statements 
ORDER BY total_time DESC LIMIT 10;

-- Reset statistics
SELECT pg_stat_statements_reset();
```

#### Health Checks

```bash
# Basic health check
podman exec todoapp-database-prod pg_isready -U todouser -d tododb

# Detailed health check
podman exec todoapp-database-prod psql -U todouser -d tododb -c "
SELECT 
  'Database Size' as metric,
  pg_size_pretty(pg_database_size('tododb')) as value
UNION ALL
SELECT 
  'Active Connections',
  COUNT(*)::text
FROM pg_stat_activity 
WHERE state = 'active'
UNION ALL
SELECT 
  'Cache Hit Ratio',
  ROUND(100.0 * sum(heap_blks_hit) / (sum(heap_blks_hit) + sum(heap_blks_read)), 2)::text || '%'
FROM pg_statio_user_tables;"
```

#### Performance Monitoring

```sql
-- Check table sizes
SELECT 
  schemaname,
  tablename,
  pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename)) as size
FROM pg_tables 
WHERE schemaname = 'public'
ORDER BY pg_total_relation_size(schemaname||'.'||tablename) DESC;

-- Check index usage
SELECT 
  indexrelname,
  idx_tup_read,
  idx_tup_fetch
FROM pg_stat_user_indexes 
ORDER BY idx_tup_read DESC;
```

## 📚 **Additional Resources**

- [PostgreSQL Documentation](https://www.postgresql.org/docs/15/)
- [Podman Documentation](https://docs.podman.io/)
- [PostgreSQL Performance Tuning](https://www.postgresql.org/docs/current/runtime-config-resource.html)
- [Container Best Practices](https://docs.docker.com/develop/dev-best-practices/)

## 🤝 **Support**

For issues or questions:
1. Check the [Troubleshooting](#troubleshooting) section
2. Review container logs: `podman logs todoapp-db`
3. Verify database connection and schema
4. Check health status: `podman exec todoapp-db pg_isready -U todouser -d tododb`
5. Create an issue in the project repository

## 📁 **File Structure**

```
database/
├── README.md                    # This comprehensive setup guide
├── Dockerfile                   # Development container with sample data
├── Dockerfile.prod              # Production container without sample data
├── postgresql.prod.conf         # Production PostgreSQL configuration
├── db/
│   ├── init/
│   │   ├── 01_create_schema.sql # Database schema and tables
│   │   ├── 02_create_indexes.sql # Performance optimization indexes
│   │   └── 03_sample_data.sql   # Development sample data (excluded in prod)
│   └── sample-data/            # Additional sample files directory
└── backup/                     # Backup files location
```

### Environment-Specific Files

| File | Development | Production | Purpose |
|------|-------------|------------|---------|
| `Dockerfile` | ✅ Used | ❌ Not used | Includes sample data for development |
| `Dockerfile.prod` | ❌ Not used | ✅ Used | Clean production image |
| `postgresql.prod.conf` | ❌ Not used | ✅ Used | Production-optimized configuration |
| `03_sample_data.sql` | ✅ Included | ❌ Excluded | Sample data for testing |

---

**Database Version**: PostgreSQL 15.13  
**Container Base**: alpine:latest  
**Last Updated**: 2024-12-19  
**Status**: ✅ Successfully deployed and tested with Podman Desktop 