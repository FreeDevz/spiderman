# Security Implementation

This document describes the security implementation for the TodoApp backend.

## Overview

The application uses Spring Security with JWT (JSON Web Tokens) for authentication and authorization. The security implementation is stateless and follows REST API best practices.

## Components

### 1. JWT Authentication Filter (`JwtAuthenticationFilter`)
- Intercepts all incoming requests
- Extracts JWT tokens from the `Authorization` header
- Validates tokens using `JwtUtil`
- Sets up Spring Security context for authenticated users

### 2. JWT Authentication Entry Point (`JwtAuthenticationEntryPoint`)
- Handles unauthorized access attempts
- Returns proper HTTP 401 responses with JSON error details
- Provides clear error messages for authentication failures

### 3. Custom User Details Service (`CustomUserDetailsService`)
- Implements Spring Security's `UserDetailsService`
- Loads user information from the database
- Maps application `User` entities to Spring Security `UserDetails`
- Handles user account status (enabled/disabled)

### 4. Authentication Manager Configuration (`AuthenticationManagerConfig`)
- Configures the authentication provider
- Sets up password encoding
- Provides authentication manager bean for the application

### 5. Security Configuration (`SecurityConfig`)
- Main security configuration class
- Defines protected and public endpoints
- Configures CORS, CSRF, and session management
- Integrates all security components

### 6. JWT Utility (`JwtUtil`)
- Handles JWT token generation and validation
- Manages both access tokens and refresh tokens
- Provides token expiration checking

## Security Flow

1. **Registration/Login**: Users register or login through `/api/auth/**` endpoints
2. **Token Generation**: Upon successful authentication, JWT tokens are generated
3. **Request Processing**: All subsequent requests include JWT token in Authorization header
4. **Token Validation**: `JwtAuthenticationFilter` validates tokens on each request
5. **Context Setup**: Valid tokens result in Spring Security context being populated
6. **Authorization**: Endpoints check user permissions based on security context

## Protected Endpoints

The following endpoints require authentication:

- `/api/tasks/**` - Task management
- `/api/categories/**` - Category management  
- `/api/tags/**` - Tag management
- `/api/users/**` - User profile management
- `/api/dashboard/**` - Dashboard data
- `/api/notifications/**` - Notification management

## Public Endpoints

The following endpoints are publicly accessible:

- `/api/auth/**` - Authentication endpoints
- `/api/health/**` - Health checks
- `/api/test/**` - Test endpoints
- `/api/simple/**` - Simple endpoints
- `/api/docs/**` - API documentation
- `/swagger-ui/**` - Swagger UI
- `/v3/api-docs/**` - OpenAPI specification
- `/actuator/health` - Actuator health endpoint
- `/actuator/info` - Actuator info endpoint

## Configuration

### JWT Configuration (application.yml)
```yaml
jwt:
  secret: ${JWT_SECRET:mySecretKey}
  expiration: 1800000  # 30 minutes
  refresh-expiration: 86400000  # 24 hours
```

### CORS Configuration
```yaml
app:
  cors:
    allowed-origins: ${CORS_ALLOWED_ORIGINS:http://localhost:3000,http://localhost:5173}
    allowed-methods: GET,POST,PUT,DELETE,PATCH,OPTIONS
    allowed-headers: "*"
    allow-credentials: true
    max-age: 3600
```

## Exception Handling

The application includes comprehensive exception handling for security-related errors:

- **AuthenticationException**: Invalid credentials or missing tokens
- **BadCredentialsException**: Wrong email/password combinations
- **AccessDeniedException**: Insufficient permissions
- **ResourceNotFoundException**: Requested resources not found
- **BusinessException**: Business rule violations

All exceptions return structured JSON responses with appropriate HTTP status codes.

## Testing

A basic security test is included (`SecurityConfigTest`) that verifies:
- Spring context loads successfully
- All security components are properly configured
- No configuration conflicts exist

## Security Best Practices

1. **Stateless Authentication**: Uses JWT tokens instead of sessions
2. **Token Expiration**: Access tokens expire after 30 minutes
3. **Refresh Tokens**: Long-lived refresh tokens for token renewal
4. **Password Hashing**: BCrypt password encoding
5. **CORS Configuration**: Properly configured for frontend integration
6. **Input Validation**: Jakarta Validation annotations on all DTOs
7. **Error Handling**: Comprehensive exception handling without information leakage

## Usage Examples

### Authentication Header
```
Authorization: Bearer <jwt_token>
```

### Error Response Format
```json
{
  "status": 401,
  "error": "Unauthorized",
  "message": "Access denied. Please provide a valid JWT token.",
  "timestamp": "2024-01-01T12:00:00"
}
```

## Future Enhancements

1. **Role-based Authorization**: Implement user roles and permissions
2. **Token Blacklisting**: Implement token revocation
3. **Rate Limiting**: Add request rate limiting
4. **Audit Logging**: Log security events
5. **Two-Factor Authentication**: Add 2FA support 