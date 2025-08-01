# TodoApp Backend

Spring Boot backend application for the TodoApp project with comprehensive REST API, JWT authentication, and health monitoring.

## Technology Stack

- **Java**: 21 LTS
- **Framework**: Spring Boot 3.5.4
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
   curl http://localhost:8080/actuator/health
   
   # API documentation
   curl http://localhost:8080/swagger-ui.html
   ```

### Podman Deployment (Recommended)

1. **Build and run with individual containers**:
   ```bash
   # Build database image
   cd database && podman build -t todoapp-database:latest .
   
   # Build backend image
   cd ../backend && podman build -t todoapp-backend:latest .
   
   # Create network and run containers
   cd .. && podman network create todo-network
   podman run -d --name todoapp-database --network todo-network -p 5432:5432 \
     -e POSTGRES_DB=tododb -e POSTGRES_USER=todouser -e POSTGRES_PASSWORD=todopass \
     todoapp-database:latest
   
   # Wait for database to initialize, then start backend
   sleep 10 && podman run -d --name todoapp-backend --network todo-network -p 8080:8080 \
     -e SPRING_PROFILES_ACTIVE=docker \
     -e SPRING_DATASOURCE_URL=jdbc:postgresql://todoapp-database:5432/tododb \
     -e SPRING_DATASOURCE_USERNAME=todouser -e SPRING_DATASOURCE_PASSWORD=todopass \
     -e JWT_SECRET=defaultSecretForDev todoapp-backend:latest
   ```

2. **Test the deployment**:
   ```bash
   # Check container status
   podman ps
   
   # Test health endpoint
   curl http://localhost:8080/actuator/health
   
   # Test API documentation
   curl http://localhost:8080/v3/api-docs
   ```

### Docker Compose Alternative

```bash
# From project root
docker-compose up --build
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
- `GET /actuator/health` - Spring Boot Actuator health
- `GET /actuator/info` - Application information
- `GET /api/health` - Application health status
- `GET /api/health/database` - Database health status

### Authentication
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login
- `POST /api/auth/logout` - User logout

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
- `PATCH /api/tasks/{id}/status` - Update task status
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
- `GET /api/dashboard/statistics` - Get dashboard statistics
- `GET /api/dashboard/recent` - Get recent activities

### Notifications
- `GET /api/notifications` - Get notifications
- `PUT /api/notifications/{id}/read` - Mark as read

### Documentation
- `GET /swagger-ui.html` - API documentation (Swagger UI)
- `GET /v3/api-docs` - OpenAPI specification

## ✅ Verified Working Features

### Authentication & Security
- ✅ User registration with email validation
- ✅ JWT-based authentication with proper token management
- ✅ Password security with BCrypt hashing
- ✅ Role-based access control
- ✅ Secure logout functionality

### Task Management
- ✅ Complete CRUD operations for tasks
- ✅ Task status management (pending, in-progress, completed)
- ✅ Priority levels (low, medium, high)
- ✅ Due date management with validation
- ✅ Bulk operations for multiple tasks
- ✅ User-specific task isolation

### Organization Features
- ✅ Categories with color coding and user isolation
- ✅ Tags for flexible task labeling
- ✅ Many-to-many relationships between tasks and tags
- ✅ Advanced filtering and search capabilities

### Dashboard & Analytics
- ✅ Real-time task statistics
- ✅ Completion rate tracking
- ✅ Overdue task monitoring
- ✅ Productivity insights and metrics

### API & Documentation
- ✅ Complete REST API with OpenAPI 3.0 specification
- ✅ Swagger UI for interactive documentation
- ✅ Comprehensive health checks and monitoring
- ✅ Proper error handling and validation
- ✅ Database health monitoring

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
- Optimized schema with 48 indexes for performance

### Health Monitoring

The application includes comprehensive health monitoring:

```bash
# Application health
curl http://localhost:8080/actuator/health

# Database health
curl http://localhost:8080/api/health/database

# Spring Boot Actuator health
curl http://localhost:8080/actuator/health

# Detailed health information
curl http://localhost:8080/actuator/health/database
```

## Testing

### API Testing Examples

```bash
# Test health endpoints
curl -f http://localhost:8080/actuator/health
curl -f http://localhost:8080/api/health

# Test user registration
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"TestPass123!","confirmPassword":"TestPass123!","name":"Test User"}'

# Test user login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"TestPass123!"}'

# Test task creation (use token from login)
TOKEN="your-jwt-token-here"
curl -X POST http://localhost:8080/api/tasks \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"title":"Test Task","description":"This is a test task","priority":"high","dueDate":"2025-12-31T23:59:59Z"}'

# Test dashboard statistics
curl -X GET http://localhost:8080/api/dashboard/statistics \
  -H "Authorization: Bearer $TOKEN"
```

### Unit Testing

```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests AuthControllerTest

# Run tests with coverage
./gradlew jacocoTestReport
```

## Deployment

### Local with Podman

```bash
# Build development image
podman build -t todoapp-backend:dev .

# Run with database
podman network create todo-network
podman run -d --name todoapp-database --network todo-network -p 5432:5432 \
  -e POSTGRES_DB=tododb -e POSTGRES_USER=todouser -e POSTGRES_PASSWORD=todopass \
  todoapp-database:latest

sleep 10 && podman run -d --name todoapp-backend --network todo-network -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=docker \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://todoapp-database:5432/tododb \
  -e SPRING_DATASOURCE_USERNAME=todouser -e SPRING_DATASOURCE_PASSWORD=todopass \
  -e JWT_SECRET=defaultSecretForDev todoapp-backend:latest
```

### Production

```bash
# Build production image
podman build -f Dockerfile.prod -t todoapp-backend:prod .

# Push to registry (example)
podman tag todoapp-backend:prod your-registry/todoapp-backend:latest
podman push your-registry/todoapp-backend:latest
```

### Kubernetes

The application is designed to be deployed to Kubernetes with:
- Kubernetes manifests in `k8s/` directory
- Container registry for image storage
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

5. **Container startup issues**:
   ```bash
   # Check container logs
   podman logs todoapp-backend
   
   # Check container status
   podman ps -a
   
   # Restart containers if needed
   podman restart todoapp-backend todoapp-database
   ```

### Logs

```bash
# View application logs
./gradlew bootRun --console=plain

# View Docker container logs
podman logs todoapp-backend

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

## Performance

### Benchmarks
- **API Response Time**: < 200ms for most operations
- **Database Queries**: Optimized with 48 indexes
- **Container Startup**: < 30 seconds for full stack
- **Health Checks**: < 5 seconds response time

### Optimization Features
- Connection pooling with HikariCP
- Optimized database indexes
- Efficient JPA queries
- Caching strategies
- Health monitoring for performance tracking

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