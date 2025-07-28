# TodoApp Database Setup Guide

This guide covers setting up the PostgreSQL database container for both **local development** and **AWS EKS production** deployment.

## 📋 **Table of Contents**

- [Overview](#overview)
- [Local Development Setup](#local-development-setup)
- [Production Deployment (AWS EKS)](#production-deployment-aws-eks)
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

## 🛠️ **Local Development Setup**

### Prerequisites

- **Podman Desktop** or **Docker Desktop**
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

### Using Docker Compose (Recommended)

From the project root directory:

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
```

## 🚀 **Production Deployment (AWS EKS)**

### Prerequisites

- **AWS CLI** configured with appropriate permissions
- **kubectl** installed and configured
- **Docker** or **Podman** for building images
- **AWS ECR** repository created

### Step 1: Build Production Image

The production environment uses a separate Dockerfile (`Dockerfile.prod`) that:
- **Excludes sample data** (no `03_sample_data.sql`)
- **Includes production configuration** (`postgresql.prod.conf`)
- **Has optimized health checks** for production monitoring
- **Contains production labels** for container management

### Step 2: Build and Push to AWS ECR

```bash
# Get ECR login token
aws ecr get-login-password --region us-west-2 | docker login --username AWS --password-stdin 123456789012.dkr.ecr.us-west-2.amazonaws.com

# Build production image
docker build -f Dockerfile.prod -t todoapp-database-prod .

# Tag for ECR
docker tag todoapp-database-prod:latest 123456789012.dkr.ecr.us-west-2.amazonaws.com/todoapp-database:latest

# Push to ECR
docker push 123456789012.dkr.ecr.us-west-2.amazonaws.com/todoapp-database:latest
```

### Step 4: Create Kubernetes Manifests

Create `k8s/database/` directory with the following manifests:

#### Secret for Database Credentials

```yaml
# k8s/database/database-secret.yaml
apiVersion: v1
kind: Secret
metadata:
  name: database-secret
  namespace: todoapp
type: Opaque
stringData:
  POSTGRES_DB: tododb
  POSTGRES_USER: todouser
  POSTGRES_PASSWORD: "your-secure-password-here"
```

#### Persistent Volume Claim

```yaml
# k8s/database/database-pvc.yaml
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: database-pvc
  namespace: todoapp
spec:
  accessModes:
    - ReadWriteOnce
  storageClassName: gp3
  resources:
    requests:
      storage: 20Gi
```

#### StatefulSet for Database

```yaml
# k8s/database/database-statefulset.yaml
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: database
  namespace: todoapp
spec:
  serviceName: database-service
  replicas: 1
  selector:
    matchLabels:
      app: database
  template:
    metadata:
      labels:
        app: database
    spec:
      containers:
      - name: postgres
        image: 123456789012.dkr.ecr.us-west-2.amazonaws.com/todoapp-database:latest
        ports:
        - containerPort: 5432
        envFrom:
        - secretRef:
            name: database-secret
        volumeMounts:
        - name: postgres-data
          mountPath: /var/lib/postgresql/data
        resources:
          requests:
            memory: "512Mi"
            cpu: "250m"
          limits:
            memory: "1Gi"
            cpu: "500m"
        livenessProbe:
          exec:
            command:
            - pg_isready
            - -U
            - todouser
            - -d
            - tododb
          initialDelaySeconds: 30
          periodSeconds: 10
        readinessProbe:
          exec:
            command:
            - pg_isready
            - -U
            - todouser
            - -d
            - tododb
          initialDelaySeconds: 5
          periodSeconds: 5
      volumes:
      - name: postgres-data
        persistentVolumeClaim:
          claimName: database-pvc
```

#### Service for Database

```yaml
# k8s/database/database-service.yaml
apiVersion: v1
kind: Service
metadata:
  name: database-service
  namespace: todoapp
spec:
  selector:
    app: database
  ports:
  - port: 5432
    targetPort: 5432
  type: ClusterIP
```

### Step 5: Deploy to EKS

```bash
# Create namespace
kubectl create namespace todoapp

# Apply database manifests
kubectl apply -f k8s/database/

# Check deployment status
kubectl get pods -n todoapp
kubectl get pvc -n todoapp
kubectl get svc -n todoapp

# View logs
kubectl logs -n todoapp -l app=database

# Check database initialization
kubectl exec -n todoapp -it deployment/database -- psql -U todouser -d tododb -c "\dt"
```

### Step 6: Verify Production Deployment

```bash
# Port forward to test connection
kubectl port-forward -n todoapp svc/database-service 5432:5432

# Test connection (in another terminal)
psql -h localhost -p 5432 -U todouser -d tododb

# Verify schema without sample data
psql -h localhost -p 5432 -U todouser -d tododb -c "SELECT COUNT(*) FROM users;"
# Should return 0 for production (no sample data)
```

## 📊 **Database Schema**

### Tables Overview

| Table | Purpose | Key Features |
|-------|---------|--------------|
| `users` | User accounts | Authentication, email verification |
| `categories` | Task organization | Color coding, user-specific |
| `tasks` | Main todo items | Status, priority, due dates |
| `tags` | Flexible labeling | Many-to-many with tasks |
| `task_tags` | Tag relationships | Junction table |
| `user_settings` | User preferences | Theme, timezone, notifications |
| `notifications` | In-app messages | Scheduled, metadata support |

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

## 🔧 **Environment Variables**

### Development

```bash
POSTGRES_DB=tododb
POSTGRES_USER=todouser
POSTGRES_PASSWORD=todopass
```

### Production

```bash
POSTGRES_DB=tododb
POSTGRES_USER=todouser
POSTGRES_PASSWORD=<secure-password>
PGDATA=/var/lib/postgresql/data
```

### AWS EKS Specific

```bash
# Use AWS Secrets Manager or Kubernetes Secrets
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
```

#### Connection Issues

```bash
# Test container networking
podman exec todoapp-db ping 127.0.0.1

# Check PostgreSQL is listening
podman exec todoapp-db netstat -tlnp | grep 5432

# Test authentication
podman exec todoapp-db psql -U todouser -d tododb -c "SELECT 1;"
```

#### Sample Data Issues

```bash
# Verify sample data loaded
podman exec todoapp-db psql -U todouser -d tododb -c "SELECT COUNT(*) FROM users;"

# Reload sample data
podman exec todoapp-db psql -U todouser -d tododb -f /docker-entrypoint-initdb.d/03_sample_data.sql
```

### Production Issues

#### Pod CrashLoopBackOff

```bash
# Check pod events
kubectl describe pod -n todoapp -l app=database

# Check logs
kubectl logs -n todoapp -l app=database --previous

# Common issues:
# 1. Image pull errors - check ECR permissions
# 2. Secret missing - verify database-secret exists
# 3. PVC issues - check storage class and permissions
```

#### Database Performance

```bash
# Check resource usage
kubectl top pod -n todoapp -l app=database

# Review slow queries
kubectl exec -n todoapp -it deployment/database -- psql -U todouser -d tododb -c "
SELECT query, calls, total_time, mean_time 
FROM pg_stat_statements 
ORDER BY total_time DESC LIMIT 10;"
```

#### Connection Issues

```bash
# Test from within cluster
kubectl run -it --rm debug --image=postgres:15-alpine --restart=Never -- psql -h database-service.todoapp.svc.cluster.local -U todouser -d tododb

# Check service endpoints
kubectl get endpoints -n todoapp database-service
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

-- Reload configuration
SELECT pg_reload_conf();
```

### Backup and Recovery

#### Development Backup

```bash
# Create backup
podman exec todoapp-db pg_dump -U todouser tododb > backup-$(date +%Y%m%d).sql

# Restore backup
podman exec -i todoapp-db psql -U todouser -d tododb < backup-20231215.sql
```

#### Production Backup (AWS EKS)

```bash
# Create backup job
kubectl create job --from=cronjob/database-backup database-backup-manual -n todoapp

# Manual backup
kubectl exec -n todoapp deployment/database -- pg_dump -U todouser tododb | gzip > backup-$(date +%Y%m%d).sql.gz
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
```

#### Health Checks

```bash
# Basic health check
pg_isready -U todouser -d tododb

# Detailed health check
psql -U todouser -d tododb -c "
SELECT 
  'Database Size' as metric,
  pg_size_pretty(pg_database_size('tododb')) as value
UNION ALL
SELECT 
  'Active Connections',
  COUNT(*)::text
FROM pg_stat_activity 
WHERE state = 'active';
"
```

## 📚 **Additional Resources**

- [PostgreSQL Documentation](https://www.postgresql.org/docs/15/)
- [Kubernetes StatefulSets](https://kubernetes.io/docs/concepts/workloads/controllers/statefulset/)
- [AWS EKS Documentation](https://docs.aws.amazon.com/eks/)
- [Podman Documentation](https://docs.podman.io/)

## 🤝 **Support**

For issues or questions:
1. Check the [Troubleshooting](#troubleshooting) section
2. Review container logs: `podman logs todoapp-db`
3. Verify database connection and schema
4. Create an issue in the project repository

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
**Last Updated**: 2024-07-28 