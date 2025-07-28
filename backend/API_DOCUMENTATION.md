# TodoApp API Documentation

## Overview

The TodoApp API is a comprehensive REST API for managing tasks, categories, tags, and user preferences. It provides JWT-based authentication and supports all CRUD operations for task management.

## Base URL

- **Development**: `http://localhost:8080`
- **Production**: `https://api.todoapp.com`
- **Staging**: `https://staging-api.todoapp.com`

## Authentication

The API uses JWT (JSON Web Tokens) for authentication. Most endpoints require a valid JWT token in the Authorization header:

```
Authorization: Bearer <your-jwt-token>
```

### Getting Started

1. **Register**: `POST /api/auth/register`
2. **Login**: `POST /api/auth/login` (returns JWT tokens)
3. **Use the access token** in subsequent requests

## API Endpoints

### Authentication (`/api/auth`)

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/register` | Register a new user | No |
| POST | `/login` | Authenticate user | No |
| POST | `/logout` | Logout user | Yes |
| POST | `/refresh` | Refresh access token | No |
| POST | `/forgot-password` | Request password reset | No |
| POST | `/reset-password` | Reset password | No |
| GET | `/verify-email` | Verify email address | No |

### Tasks (`/api/tasks`)

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/` | Get all tasks (with filtering) | Yes |
| POST | `/` | Create a new task | Yes |
| GET | `/{id}` | Get task by ID | Yes |
| PUT | `/{id}` | Update task | Yes |
| DELETE | `/{id}` | Delete task | Yes |
| PATCH | `/{id}/status` | Update task status | Yes |
| POST | `/bulk` | Bulk operations | Yes |
| GET | `/export` | Export tasks | Yes |
| POST | `/import` | Import tasks | Yes |

### Categories (`/api/categories`)

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/` | Get all categories | Yes |
| POST | `/` | Create a new category | Yes |
| PUT | `/{id}` | Update category | Yes |
| DELETE | `/{id}` | Delete category | Yes |

### Tags (`/api/tags`)

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/` | Get all tags | Yes |
| POST | `/` | Create a new tag | Yes |
| PUT | `/{id}` | Update tag | Yes |
| DELETE | `/{id}` | Delete tag | Yes |

### Users (`/api/users`)

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/profile` | Get user profile | Yes |
| PUT | `/profile` | Update user profile | Yes |
| DELETE | `/account` | Delete user account | Yes |
| GET | `/settings` | Get user settings | Yes |
| PUT | `/settings` | Update user settings | Yes |

### Dashboard (`/api/dashboard`)

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/statistics` | Get dashboard statistics | Yes |
| GET | `/today` | Get today's tasks | Yes |
| GET | `/upcoming` | Get upcoming tasks | Yes |
| GET | `/overdue` | Get overdue tasks | Yes |
| GET | `/activity` | Get recent activity | Yes |

### Notifications (`/api/notifications`)

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/` | Get all notifications | Yes |
| POST | `/{id}/read` | Mark notification as read | Yes |
| PUT | `/settings` | Update notification settings | Yes |

### Health (`/health`)

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/` | Application health check | No |
| GET | `/database` | Database health check | No |

## Data Models

### Task
```json
{
  "id": 1,
  "title": "Complete project",
  "description": "Finish the todo app project",
  "status": "IN_PROGRESS",
  "priority": "HIGH",
  "dueDate": "2024-01-15T10:00:00Z",
  "categoryId": 1,
  "tagIds": [1, 2],
  "createdAt": "2024-01-01T10:00:00Z",
  "updatedAt": "2024-01-01T10:00:00Z"
}
```

### Category
```json
{
  "id": 1,
  "name": "Work",
  "color": "#FF5733",
  "description": "Work-related tasks",
  "createdAt": "2024-01-01T10:00:00Z"
}
```

### Tag
```json
{
  "id": 1,
  "name": "urgent",
  "color": "#FF0000",
  "createdAt": "2024-01-01T10:00:00Z"
}
```

### User
```json
{
  "id": 1,
  "email": "user@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "createdAt": "2024-01-01T10:00:00Z"
}
```

## Error Responses

The API returns consistent error responses with appropriate HTTP status codes:

```json
{
  "timestamp": "2024-01-01T10:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/tasks"
}
```

### Common Status Codes

- `200` - Success
- `201` - Created
- `400` - Bad Request
- `401` - Unauthorized
- `403` - Forbidden
- `404` - Not Found
- `409` - Conflict
- `500` - Internal Server Error

## Rate Limiting

API requests are rate-limited to ensure fair usage:
- **Authenticated users**: 1000 requests per hour
- **Unauthenticated users**: 100 requests per hour

## CORS

The API supports CORS for cross-origin requests from:
- `http://localhost:3000` (development)
- `http://localhost:5173` (Vite development)
- `https://todoapp.com` (production)

## Swagger UI

Interactive API documentation is available at:
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/api-docs`

## Examples

### Creating a Task

```bash
curl -X POST http://localhost:8080/api/tasks \
  -H "Authorization: Bearer <your-token>" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Complete API documentation",
    "description": "Write comprehensive API docs",
    "priority": "HIGH",
    "dueDate": "2024-01-15T10:00:00Z",
    "categoryId": 1,
    "tagIds": [1, 2]
  }'
```

### Getting Tasks with Filters

```bash
curl -X GET "http://localhost:8080/api/tasks?status=TODO&priority=HIGH&page=0&size=10" \
  -H "Authorization: Bearer <your-token>"
```

### Updating Task Status

```bash
curl -X PATCH http://localhost:8080/api/tasks/1/status \
  -H "Authorization: Bearer <your-token>" \
  -H "Content-Type: application/json" \
  -d '{"status": "COMPLETED"}'
```

## Support

For API support and questions:
- **Email**: support@todoapp.com
- **Documentation**: https://todoapp.com/docs
- **GitHub**: https://github.com/todoapp/api

## License

This API is licensed under the MIT License. 