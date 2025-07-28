# TodoApp Backend

Spring Boot backend application for the TodoApp project with comprehensive REST API, JWT authentication, and health monitoring.

## Technology Stack

- **Java**: 21 LTS
- **Framework**: Spring Boot 3.1.5
- **Build Tool**: Gradle 8.5 (Kotlin DSL)
- **Database**: PostgreSQL 15+
- **Security**: Spring Security + JWT
- **Documentation**: OpenAPI 3 (Swagger)
- **Containerization**: Docker/Podman
- **Health Monitoring**: Spring Boot Actuator + Custom Health Indicators

## Prerequisites

- Java 21+
- Gradle 8.5+
- Docker/Podman (for containerized deployment)
- PostgreSQL (for local development)

## Quick Start

### Local Development

1. **Start the database** (using Podman/Docker):
   ```bash
   # From project root
   docker-compose up database --build
   ```

2. **Run the backend application**:
   ```bash
   # From backend directory
   ./gradlew bootRun
   ```

3. **Test the application**:
   ```bash
   # Health check
   curl http://localhost:8080/api/health
   
   # API documentation
   curl http://localhost:8080/swagger-ui.html
   ```

### Docker Development

1. **Build and run with Docker Compose**:
   ```bash
   # From project root
   docker-compose up --build
   ```

2. **Or run backend only**:
   ```bash
   docker-compose up backend --build
   ```

## Docker Images

### Development Image (`Dockerfile`)

- **Purpose**: Local development and testing
- **Features**:
  - Includes Gradle wrapper for hot reloading
  - Runs with `./gradlew bootRun`
  - Faster health checks (10s intervals)
  - Includes development tools
  - Sample data support

### Production Image (`Dockerfile.prod`)

- **Purpose**: Production deployment
- **Features**:
  - Multi-stage build for smaller image size
  - Runs compiled JAR file
  - Non-root user for security
  - Optimized JVM settings
  - Slower health checks (30s intervals)
  - Production-optimized configuration

## Environment Configuration

### Profiles

- **default**: Local development with H2 database
- **dev**: Development with PostgreSQL
- **docker**: Containerized environment
- **test**: Testing with H2 database

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `SPRING_PROFILES_ACTIVE` | Active Spring profile | `default` |
| `SPRING_DATASOURCE_URL` | Database connection URL | `jdbc:h2:mem:testdb` |
| `SPRING_DATASOURCE_USERNAME` | Database username | `sa` |
| `SPRING_DATASOURCE_PASSWORD` | Database password | `` |
| `JWT_SECRET` | JWT signing secret | `defaultSecretForDev` |
| `SERVER_PORT` | Application port | `8080` |
| `MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE` | Exposed health endpoints | `health,info` |

## API Endpoints

### Health Checks
- `GET /api/health` - Application health status
- `GET /api/health/database` - Database health status
- `GET /actuator/health` - Spring Boot Actuator health
- `GET /actuator/info` - Application information

### Authentication
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login
- `POST /api/auth/refresh` - Refresh JWT token

### User Management
- `GET /api/users/profile` - Get user profile
- `PUT /api/users/profile` - Update user profile
- `DELETE /api/users/account` - Delete user account
- `GET /api/users/settings` - Get user settings
- `PUT /api/users/settings` - Update user settings

### Task Management
- `GET /api/tasks` - Get all tasks (with filtering)
- `POST /api/tasks` - Create new task
- `GET /api/tasks/{id}` - Get task by ID
- `PUT /api/tasks/{id}` - Update task
- `DELETE /api/tasks/{id}` - Delete task
- `POST /api/tasks/bulk` - Bulk operations

### Categories
- `GET /api/categories` - Get all categories
- `POST /api/categories` - Create category
- `PUT /api/categories/{id}` - Update category
- `DELETE /api/categories/{id}` - Delete category

### Tags
- `GET /api/tags` - Get all tags
- `POST /api/tags` - Create tag
- `PUT /api/tags/{id}` - Update tag
- `DELETE /api/tags/{id}` - Delete tag

### Dashboard
- `GET /api/dashboard/stats` - Get dashboard statistics
- `GET /api/dashboard/recent` - Get recent activities

### Notifications
- `GET /api/notifications` - Get notifications
- `PUT /api/notifications/{id}/read` - Mark as read

### Documentation
- `GET /swagger-ui.html` - API documentation (Swagger UI)
- `GET /v3/api-docs` - OpenAPI specification

## Development

### Project Structure

```
src/
├── main/
│   ├── java/com/todoapp/
│   │   ├── config/          # Configuration classes
│   │   │   ├── OpenApiConfig.java
│   │   │   ├── SecurityConfig.java
│   │   │   ├── PasswordConfig.java
│   │   │   └── DatabaseHealthIndicator.java
│   │   ├── controller/      # REST controllers
│   │   │   ├── AuthController.java
│   │   │   ├── TaskController.java
│   │   │   ├── CategoryController.java
│   │   │   ├── TagController.java
│   │   │   ├── UserController.java
│   │   │   ├── DashboardController.java
│   │   │   ├── NotificationController.java
│   │   │   ├── HealthController.java
│   │   │   └── SimpleController.java
│   │   ├── dto/            # Data Transfer Objects
│   │   │   ├── AuthRequest.java
│   │   │   ├── AuthResponse.java
│   │   │   ├── TaskDTO.java
│   │   │   ├── CategoryDTO.java
│   │   │   ├── TagDTO.java
│   │   │   ├── UserDTO.java
│   │   │   └── DashboardStatisticsDTO.java
│   │   ├── entity/         # JPA entities
│   │   │   ├── User.java
│   │   │   ├── Task.java
│   │   │   ├── Category.java
│   │   │   ├── Tag.java
│   │   │   ├── UserSettings.java
│   │   │   └── Notification.java
│   │   ├── exception/      # Custom exceptions
│   │   │   ├── BusinessException.java
│   │   │   ├── ResourceNotFoundException.java
│   │   │   └── GlobalExceptionHandler.java
│   │   ├── repository/     # Data access layer
│   │   │   ├── UserRepository.java
│   │   │   ├── TaskRepository.java
│   │   │   ├── CategoryRepository.java
│   │   │   ├── TagRepository.java
│   │   │   ├── UserSettingsRepository.java
│   │   │   └── NotificationRepository.java
│   │   ├── security/       # Security configuration
│   │   │   ├── JwtAuthenticationFilter.java
│   │   │   ├── JwtAuthenticationEntryPoint.java
│   │   │   ├── CustomUserDetailsService.java
│   │   │   └── AuthenticationManagerConfig.java
│   │   ├── service/        # Business logic
│   │   │   ├── AuthService.java
│   │   │   ├── TaskService.java
│   │   │   ├── CategoryService.java
│   │   │   ├── TagService.java
│   │   │   ├── UserService.java
│   │   │   ├── DashboardService.java
│   │   │   └── NotificationService.java
│   │   └── util/           # Utility classes
│   │       └── JwtUtil.java
│   └── resources/
│       ├── application.yml # Application configuration
│       └── db/             # Database scripts
└── test/                   # Test classes
    ├── java/com/todoapp/
    │   └── security/
    │       └── SecurityConfigTest.java
    └── resources/
        └── application-test.yml
```

### Building

```bash
# Compile
./gradlew compileJava

# Run tests
./gradlew test

# Build JAR
./gradlew build

# Build Docker image
./gradlew dockerBuild
```

### Database

The application uses PostgreSQL with the following features:
- JPA/Hibernate for ORM
- Flyway for migrations (disabled, using init scripts)
- Connection pooling with HikariCP
- Auditing with `@EntityListeners`
- Health monitoring with custom `DatabaseHealthIndicator`

### Health Monitoring

The application includes comprehensive health monitoring:

```bash
# Application health
curl http://localhost:8080/api/health

# Database health
curl http://localhost:8080/api/health/database

# Spring Boot Actuator health
curl http://localhost:8080/actuator/health

# Detailed health information
curl http://localhost:8080/actuator/health/database
```

## Deployment

### Local with Docker

```bash
# Build development image
docker build -t todoapp-backend:dev .

# Run with database
docker-compose up --build
```

### Production

```bash
# Build production image
docker build -f Dockerfile.prod -t todoapp-backend:prod .

# Push to registry (example)
docker tag todoapp-backend:prod your-registry/todoapp-backend:latest
docker push your-registry/todoapp-backend:latest
```

### Kubernetes

The application is designed to be deployed to AWS EKS with:
- Kubernetes manifests in `k8s/` directory
- AWS ECR for container registry
- Load balancer and ingress configuration
- Secrets management for sensitive data
- Health check probes for container orchestration

## Troubleshooting

### Common Issues

1. **Port already in use**:
   ```bash
   # Check what's using port 8080
   lsof -i :8080
   # Kill the process
   pkill -f "TodoAppApplication"
   ```

2. **Database connection issues**:
   - Ensure PostgreSQL container is running
   - Check database credentials in `application.yml`
   - Verify network connectivity between containers
   - Check health endpoint: `curl http://localhost:8080/api/health/database`

3. **Build failures**:
   ```bash
   # Clean and rebuild
   ./gradlew clean build
   ```

4. **JWT authentication issues**:
   - Verify JWT_SECRET environment variable
   - Check token expiration settings
   - Validate token format in Authorization header

### Logs

```bash
# View application logs
./gradlew bootRun --console=plain

# View Docker container logs
docker logs todoapp-backend

# View health check logs
curl http://localhost:8080/actuator/health
```

### Health Check Diagnostics

```bash
# Check application status
curl -s http://localhost:8080/api/health | jq

# Check database connectivity
curl -s http://localhost:8080/api/health/database | jq

# Check all health indicators
curl -s http://localhost:8080/actuator/health | jq
```

## Documentation

### Additional Documentation Files

- **[API Documentation](API_DOCUMENTATION.md)** - Complete REST API reference
- **[Security Implementation](SECURITY_README.md)** - JWT authentication and security details
- **[Health Checks](HEALTH_CHECK_README.md)** - Health monitoring and diagnostics
- **[API Implementation Status](API_IMPLEMENTATION_STATUS.md)** - Current implementation progress
- **[Swagger Documentation](SWAGGER_DOCUMENTATION_SUMMARY.md)** - OpenAPI/Swagger integration

## Contributing

1. Follow the existing code structure
2. Add tests for new features
3. Update documentation as needed
4. Use meaningful commit messages
5. Ensure health checks are implemented for new services

## License

This project is part of the TodoApp application. 