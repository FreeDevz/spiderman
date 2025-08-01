# TodoApp Frontend

Modern React TypeScript frontend application for the TodoApp project, built with Vite, Redux Toolkit, and Tailwind CSS.

## Technology Stack

- **Framework**: React 18+ with TypeScript
- **Build Tool**: Vite 5.0+
- **State Management**: Redux Toolkit
- **Styling**: Tailwind CSS
- **UI Components**: Custom components with modern design
- **HTTP Client**: Axios for API communication
- **Routing**: React Router for navigation
- **Testing**: Jest and React Testing Library
- **Containerization**: Docker/Podman

## Prerequisites

- Node.js 18+
- npm or yarn package manager
- Podman/Docker (for containerized deployment)

## Quick Start

### Local Development

1. **Install dependencies**:
   ```bash
   cd frontend
   npm install
   ```

2. **Start development server**:
   ```bash
   npm run dev
   ```

3. **Access the application**:
   - **Frontend**: http://localhost:5173
   - **Backend API**: http://localhost:8080/api (ensure backend is running)

### Containerized Development

1. **Build and run with Podman**:
   ```bash
   # Build frontend image
   cd frontend && podman build -t todoapp-frontend:latest .
   
   # Run frontend container
   podman run -d --name todoapp-frontend --network todo-network -p 3000:80 \
     -e REACT_APP_API_URL=http://localhost:8080/api \
     todoapp-frontend:latest
   ```

2. **Access the application**:
   - **Frontend**: http://localhost:3000
   - **Backend API**: http://localhost:8080/api

## Project Structure

```
frontend/
├── public/                 # Static assets
│   ├── manifest.json      # PWA manifest
│   └── vite.svg           # Vite logo
├── src/
│   ├── components/        # Reusable UI components
│   │   ├── common/        # Common components (Button, Input, etc.)
│   │   ├── forms/         # Form components
│   │   ├── layout/        # Layout components (Header, Sidebar, etc.)
│   │   └── tasks/         # Task-specific components
│   ├── pages/             # Page components
│   │   ├── auth/          # Authentication pages
│   │   ├── dashboard/     # Dashboard page
│   │   └── tasks/         # Task management pages
│   ├── services/          # API services
│   │   ├── api.ts         # Base API configuration
│   │   ├── authService.ts # Authentication service
│   │   ├── taskService.ts # Task management service
│   │   ├── categoryService.ts # Category service
│   │   └── tagService.ts  # Tag service
│   ├── store/             # Redux store
│   │   ├── index.ts       # Store configuration
│   │   ├── hooks.ts       # Redux hooks
│   │   └── slices/        # Redux slices
│   │       ├── authSlice.ts
│   │       ├── taskSlice.ts
│   │       ├── categorySlice.ts
│   │       ├── tagSlice.ts
│   │       └── uiSlice.ts
│   ├── types/             # TypeScript type definitions
│   ├── utils/             # Utility functions
│   ├── styles/            # Global styles
│   ├── constants/         # Application constants
│   ├── hooks/             # Custom React hooks
│   ├── App.tsx            # Main application component
│   ├── main.tsx           # Application entry point
│   └── index.css          # Global CSS
├── Dockerfile             # Container build configuration
├── nginx.conf             # Nginx configuration for production
├── package.json           # Dependencies and scripts
├── tsconfig.json          # TypeScript configuration
├── tailwind.config.js     # Tailwind CSS configuration
├── postcss.config.js      # PostCSS configuration
├── eslint.config.js       # ESLint configuration
└── vite.config.ts         # Vite configuration
```

## Features

### ✅ Implemented Features

#### Authentication & User Management
- User registration and login forms
- JWT token management
- Protected routes
- User profile management
- Logout functionality

#### Task Management
- Create, edit, delete tasks
- Task status management (pending, in-progress, completed)
- Priority levels with visual indicators
- Due date management with calendar integration
- Task filtering and search
- Bulk operations for multiple tasks

#### Organization Features
- Category management with color coding
- Tag system for flexible task labeling
- Advanced filtering by status, priority, due date, and tags
- User-specific data isolation

#### Dashboard & Analytics
- Real-time task statistics
- Completion rate visualization
- Overdue task monitoring
- Productivity insights and charts

#### UI/UX Features
- Responsive design for all devices
- Dark/light theme support
- Modern, clean interface
- Loading states and error handling
- Accessibility features

### 🚧 In Progress Features

#### Advanced Features
- Real-time synchronization
- Offline support with service workers
- Email notifications
- Data export/import functionality
- Advanced analytics and reporting

#### User Experience
- Drag-and-drop task reordering
- Keyboard shortcuts
- Auto-save functionality
- Progressive Web App (PWA) features

## Development

### Available Scripts

```bash
# Development
npm run dev          # Start development server
npm run build        # Build for production
npm run preview      # Preview production build

# Testing
npm run test         # Run unit tests
npm run test:e2e     # Run end-to-end tests
npm run test:coverage # Run tests with coverage

# Code Quality
npm run lint         # Run ESLint
npm run lint:fix     # Fix ESLint issues
npm run type-check   # TypeScript type checking

# Container
npm run docker:build # Build Docker image
npm run docker:run   # Run Docker container
```

### Environment Variables

Create a `.env` file in the frontend directory:

```bash
# API Configuration
VITE_API_BASE_URL=http://localhost:8080/api
VITE_APP_NAME=TodoApp
VITE_APP_VERSION=1.0.0

# Feature Flags
VITE_ENABLE_PWA=true
VITE_ENABLE_ANALYTICS=false

# Development
VITE_DEV_MODE=true
```

### API Integration

The frontend integrates with the backend API through service modules:

```typescript
// Example API service usage
import { taskService } from '../services/taskService';

// Get all tasks
const tasks = await taskService.getTasks();

// Create a new task
const newTask = await taskService.createTask({
  title: 'New Task',
  description: 'Task description',
  priority: 'high',
  dueDate: '2025-12-31T23:59:59Z'
});
```

### State Management

Redux Toolkit is used for state management with the following slices:

- **authSlice**: User authentication and profile
- **taskSlice**: Task management and filtering
- **categorySlice**: Category management
- **tagSlice**: Tag management
- **uiSlice**: UI state (theme, loading, modals)

### Styling

The application uses Tailwind CSS for styling with:
- Custom color palette
- Responsive design utilities
- Component-based styling
- Dark/light theme support

## Testing

### Unit Testing

```bash
# Run all tests
npm run test

# Run tests in watch mode
npm run test:watch

# Run tests with coverage
npm run test:coverage

# Run specific test file
npm run test -- --testPathPattern=TaskComponent
```

### End-to-End Testing

```bash
# Run E2E tests
npm run test:e2e

# Run E2E tests in headless mode
npm run test:e2e:headless
```

### Testing Strategy

- **Unit Tests**: Component testing with React Testing Library
- **Integration Tests**: API service testing
- **E2E Tests**: User workflow testing with Cypress
- **Visual Regression**: Component visual testing

## Deployment

### Production Build

```bash
# Build for production
npm run build

# Preview production build
npm run preview
```

### Container Deployment

```bash
# Build production image
podman build -t todoapp-frontend:prod .

# Run production container
podman run -d --name todoapp-frontend-prod -p 3000:80 \
  -e REACT_APP_API_URL=https://your-api-domain.com/api \
  todoapp-frontend:prod
```

### Static Hosting

The application can be deployed to various static hosting platforms:

- **Netlify**: Connect to Git repository for automatic deployments
- **Vercel**: Zero-config deployment with Git integration
- **AWS S3 + CloudFront**: Static website hosting
- **GitHub Pages**: Free hosting for open source projects

## Performance

### Optimization Features

- **Code Splitting**: Automatic route-based code splitting
- **Lazy Loading**: Component lazy loading for better performance
- **Image Optimization**: Optimized image loading and caching
- **Bundle Analysis**: Webpack bundle analyzer for optimization
- **Caching**: Service worker caching for offline support

### Performance Metrics

- **First Contentful Paint**: < 1.5s
- **Largest Contentful Paint**: < 2.5s
- **Cumulative Layout Shift**: < 0.1
- **First Input Delay**: < 100ms

## Browser Support

| Browser | Version | Status |
|---------|---------|--------|
| Chrome  | 90+     | ✅ Full Support |
| Firefox | 88+     | ✅ Full Support |
| Safari  | 14+     | ✅ Full Support |
| Edge    | 90+     | ✅ Full Support |

## Accessibility

The application follows WCAG 2.1 guidelines:

- **Keyboard Navigation**: Full keyboard accessibility
- **Screen Reader Support**: ARIA labels and semantic HTML
- **Color Contrast**: High contrast ratios for readability
- **Focus Management**: Proper focus indicators and management
- **Alternative Text**: Descriptive alt text for images

## Contributing

### Development Workflow

1. **Fork** the repository
2. **Create** a feature branch (`git checkout -b feature/amazing-feature`)
3. **Install** dependencies (`npm install`)
4. **Make** your changes
5. **Test** your changes (`npm run test`)
6. **Commit** your changes (`git commit -m 'Add amazing feature'`)
7. **Push** to the branch (`git push origin feature/amazing-feature`)
8. **Open** a Pull Request

### Code Standards

- **TypeScript**: Strict type checking enabled
- **ESLint**: Code linting with React-specific rules
- **Prettier**: Code formatting
- **Conventional Commits**: Standardized commit messages
- **Testing**: Minimum 80% code coverage

## Troubleshooting

### Common Issues

1. **Build Failures**:
   ```bash
   # Clear cache and reinstall dependencies
   rm -rf node_modules package-lock.json
   npm install
   ```

2. **API Connection Issues**:
   - Verify backend is running on correct port
   - Check CORS configuration in backend
   - Verify API base URL in environment variables

3. **Container Issues**:
   ```bash
   # Check container logs
   podman logs todoapp-frontend
   
   # Rebuild container
   podman build --no-cache -t todoapp-frontend:latest .
   ```

4. **Performance Issues**:
   - Run bundle analysis: `npm run analyze`
   - Check for memory leaks in development tools
   - Optimize images and assets

## Documentation

### Additional Resources

- **[API Documentation](http://localhost:8080/swagger-ui.html)** - Backend API reference
- **[Component Library](docs/components.md)** - UI component documentation
- **[State Management](docs/state-management.md)** - Redux store documentation
- **[Testing Guide](docs/testing.md)** - Testing strategies and examples

## License

This project is part of the TodoApp application and is licensed under the MIT License.

---

**Built with ❤️ for productivity enthusiasts**

*TodoApp Frontend - Making task management beautiful and efficient*
