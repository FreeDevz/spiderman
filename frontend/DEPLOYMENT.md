# Frontend Deployment Guide

This document explains how to build and deploy the TodoApp frontend to Podman.

## 🚀 Quick Deployment

### Using the Deployment Script (Recommended)

```bash
# Development deployment (default)
./deploy.sh

# Production deployment
./deploy.sh --prod

# Development deployment (explicit)
./deploy.sh --dev
```

### Manual Deployment

```bash
# 1. Install dependencies
npm install

# 2. Build the application
npm run build

# 3. Build Docker image
podman build -t todoapp-frontend:latest .

# 4. Run container
podman run -d --name todoapp-frontend --network todo-network -p 3000:80 todoapp-frontend:latest
```

## 🐳 Docker Images

### Development Image (`Dockerfile`)
- Simple nginx-based image
- Copies pre-built `dist` folder
- Suitable for development and testing

### Production Image (`Dockerfile.prod`)
- Multi-stage build for optimization
- Builds application inside container
- Includes health checks
- More secure and optimized

## 🌐 Access Points

- **Frontend Application**: http://localhost:3000
- **Backend API**: http://localhost:8080
- **Health Check**: http://localhost:3000/health
- **API Documentation**: http://localhost:8080/swagger-ui.html

## 🔧 Container Management

```bash
# View running containers
podman ps

# View container logs
podman logs todoapp-frontend

# Stop container
podman stop todoapp-frontend

# Remove container
podman rm todoapp-frontend

# Restart container
podman restart todoapp-frontend
```

## 🏗️ Build Process

1. **Dependencies**: Installs npm packages
2. **TypeScript**: Compiles TypeScript to JavaScript
3. **Vite Build**: Bundles and optimizes for production
4. **Docker Build**: Creates container image
5. **Deployment**: Stops old container and starts new one
6. **Health Check**: Verifies deployment success

## 📦 Build Output

The build process creates:
- `dist/index.html` - Main HTML file
- `dist/assets/index-*.css` - Compiled CSS with Tailwind
- `dist/assets/index-*.js` - Bundled JavaScript
- `dist/vite.svg` - Application icon

## 🔒 Security Features

- **Security Headers**: XSS protection, content type options
- **CORS Configuration**: Proper cross-origin settings
- **Gzip Compression**: Optimized file delivery
- **Cache Headers**: Efficient static asset caching

## 🚨 Troubleshooting

### Build Issues
```bash
# Clear npm cache
npm cache clean --force

# Remove node_modules and reinstall
rm -rf node_modules package-lock.json
npm install
```

### Container Issues
```bash
# Check container logs
podman logs todoapp-frontend

# Check container status
podman ps -a

# Restart container
podman restart todoapp-frontend
```

### Network Issues
```bash
# Check network connectivity
podman network ls
podman network inspect todo-network

# Test API connectivity
curl http://localhost:8080/actuator/health
```

## 📊 Performance

- **Build Time**: ~2 seconds
- **Bundle Size**: ~474KB (gzipped: ~144KB)
- **CSS Size**: ~13KB (gzipped: ~3.4KB)
- **Startup Time**: <5 seconds

## 🔄 CI/CD Integration

The deployment script can be integrated into CI/CD pipelines:

```yaml
# Example GitHub Actions step
- name: Deploy Frontend
  run: |
    cd frontend
    chmod +x deploy.sh
    ./deploy.sh --prod
```

## 📝 Environment Variables

The frontend can be configured with environment variables:

```bash
# API Base URL (defaults to /api/)
VITE_API_BASE_URL=http://localhost:8080/api

# Environment (development/production)
NODE_ENV=production
```

## 🎨 Design System

The deployed frontend includes:
- **shadcn/ui Components**: Modern, accessible UI components
- **Custom Color Palette**: Beautiful gradients and colors
- **App Logo**: Custom SVG logo with gradients
- **Responsive Design**: Mobile-first approach
- **Smooth Animations**: Fade-in, slide-up, and scale effects 