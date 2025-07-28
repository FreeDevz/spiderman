# Health Check Implementation

This document describes the health check implementation for the TodoApp backend service.

## Overview

The application uses Spring Boot Actuator for health monitoring with custom health indicators for comprehensive system monitoring.

## Health Endpoints

### 1. Spring Boot Actuator Health Endpoint
- **URL**: `/actuator/health`
- **Method**: GET
- **Authentication**: Optional (basic auth: admin/admin for detailed info)
- **Response**: Overall system health status

### 2. Custom Application Health Endpoint
- **URL**: `/health`
- **Method**: GET
- **Authentication**: Not required
- **Response**: Application-specific health information

### 3. Custom Database Health Endpoint
- **URL**: `/health/database`
- **Method**: GET
- **Authentication**: Not required
- **Response**: Detailed database health information

## Health Indicators

### Built-in Indicators
- **Disk Space**: Monitors available disk space
- **Ping**: Basic connectivity check
- **SSL**: SSL certificate validation
- **Database**: Connection pool health

### Custom Database Health Indicator
Located in `com.todoapp.config.DatabaseHealthIndicator`

**Features:**
- Database connectivity test
- Response time measurement
- Database statistics collection:
  - Database version
  - Active connections count
  - Database size
  - Table count
- Detailed error reporting

**Response Example:**
```json
{
  "database": "UP",
  "timestamp": "2025-07-28T13:59:25.883798083",
  "details": {
    "table_count": 8,
    "database_version": "PostgreSQL",
    "response_time_ms": 2,
    "database_size": "8621 kB",
    "message": "Database connection is healthy",
    "active_connections": 1,
    "status": "UP"
  }
}
```

## Configuration

### Actuator Configuration
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
      base-path: /actuator
  endpoint:
    health:
      show-details: when-authorized
      show-components: always
  health:
    mail:
      enabled: false  # Disabled mail health check
    defaults:
      enabled: true
```

### Health Check Security
- Basic authentication required for detailed health information
- Default credentials: admin/admin
- Configured in `application.yml` under `spring.security.user`

## Troubleshooting

### Common Issues

1. **Health Check Returns 503**
   - Check database connectivity
   - Verify database credentials
   - Review application logs

2. **Mail Health Check Errors**
   - Mail functionality is disabled by default
   - Remove `spring-boot-starter-mail` dependency if not needed
   - Configure mail settings if email functionality is required

3. **Database Connection Issues**
   - Verify database container is running
   - Check database credentials in environment variables
   - Ensure database schema is properly initialized

### Debugging Commands

```bash
# Check container status
podman compose ps

# View application logs
podman compose logs backend

# Test health endpoints
curl http://localhost:8080/actuator/health
curl http://localhost:8080/health
curl http://localhost:8080/health/database

# Test with authentication
curl -u admin:admin http://localhost:8080/actuator/health

# Test database connectivity directly
podman exec todoapp-database psql -U todouser -d tododb -c "SELECT 1;"
```

## Monitoring Integration

### Kubernetes Health Checks
The application is configured for Kubernetes health checks:

```yaml
livenessProbe:
  httpGet:
    path: /actuator/health
    port: 8080
  initialDelaySeconds: 60
  periodSeconds: 30

readinessProbe:
  httpGet:
    path: /actuator/health/readiness
    port: 8080
  initialDelaySeconds: 30
  periodSeconds: 10
```

### Docker Health Check
```dockerfile
HEALTHCHECK --interval=10s --timeout=5s --retries=5 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1
```

## Future Enhancements

1. **Additional Health Indicators**
   - External service connectivity
   - Cache health (Redis/Memcached)
   - File system health
   - JVM metrics

2. **Custom Metrics**
   - Application-specific metrics
   - Business logic health checks
   - Performance monitoring

3. **Alerting Integration**
   - Prometheus metrics export
   - Grafana dashboard integration
   - Slack/Email notifications

## Security Considerations

- Health endpoints should be protected in production
- Consider using different authentication for health checks
- Implement rate limiting for health endpoints
- Use HTTPS in production environments
- Regularly rotate health check credentials 