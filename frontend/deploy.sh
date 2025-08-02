#!/bin/bash

# Frontend Deployment Script for TodoApp
# This script builds and deploys the frontend to Podman

set -e  # Exit on any error

echo "🚀 Starting TodoApp Frontend Deployment..."

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if we're in the frontend directory
if [ ! -f "package.json" ]; then
    print_error "Please run this script from the frontend directory"
    exit 1
fi

# Parse command line arguments
BUILD_TYPE="dev"
DOCKERFILE="Dockerfile"
IMAGE_TAG="latest"

while [[ $# -gt 0 ]]; do
    case $1 in
        --prod|--production)
            BUILD_TYPE="prod"
            DOCKERFILE="Dockerfile.prod"
            IMAGE_TAG="prod"
            shift
            ;;
        --dev|--development)
            BUILD_TYPE="dev"
            DOCKERFILE="Dockerfile"
            IMAGE_TAG="latest"
            shift
            ;;
        *)
            print_warning "Unknown option: $1"
            shift
            ;;
    esac
done

print_status "Build type: $BUILD_TYPE"
print_status "Dockerfile: $DOCKERFILE"
print_status "Image tag: $IMAGE_TAG"

# Step 1: Install dependencies
print_status "Installing dependencies..."
npm install

# Step 2: Build the application
print_status "Building the application..."
npm run build

if [ $? -eq 0 ]; then
    print_success "Build completed successfully!"
else
    print_error "Build failed!"
    exit 1
fi

# Step 3: Build Docker image
print_status "Building Docker image using $DOCKERFILE..."
if [ "$BUILD_TYPE" = "prod" ]; then
    podman build -f $DOCKERFILE -t todoapp-frontend:$IMAGE_TAG .
else
    podman build -t todoapp-frontend:$IMAGE_TAG .
fi

if [ $? -eq 0 ]; then
    print_success "Docker image built successfully!"
else
    print_error "Docker build failed!"
    exit 1
fi

# Step 4: Stop and remove existing container (if running)
print_status "Stopping existing frontend container..."
podman stop todoapp-frontend 2>/dev/null || print_warning "No existing frontend container to stop"
podman rm todoapp-frontend 2>/dev/null || print_warning "No existing frontend container to remove"

# Step 5: Start new container
print_status "Starting new frontend container..."
podman run -d --name todoapp-frontend --network todo-network -p 3000:80 todoapp-frontend:$IMAGE_TAG

if [ $? -eq 0 ]; then
    print_success "Frontend container started successfully!"
else
    print_error "Failed to start frontend container!"
    exit 1
fi

# Step 6: Wait a moment for container to start
sleep 3

# Step 7: Verify deployment
print_status "Verifying deployment..."
if curl -f -s http://localhost:3000/health > /dev/null; then
    print_success "Frontend is healthy and responding!"
else
    print_error "Frontend health check failed!"
    exit 1
fi

# Step 8: Show container status
print_status "Container status:"
podman ps --filter name=todoapp-frontend

echo ""
print_success "🎉 Frontend deployment completed successfully!"
echo ""
echo "📱 Frontend URL: http://localhost:3000"
echo "🔗 Backend API: http://localhost:8080"
echo "📊 Health Check: http://localhost:3000/health"
echo "🏷️  Image tag: todoapp-frontend:$IMAGE_TAG"
echo ""
echo "📋 Useful commands:"
echo "  View logs: podman logs todoapp-frontend"
echo "  Stop container: podman stop todoapp-frontend"
echo "  Restart container: podman restart todoapp-frontend"
echo "  View all containers: podman ps"
echo ""
echo "🔄 To deploy with different settings:"
echo "  Development: ./deploy.sh --dev"
echo "  Production: ./deploy.sh --prod" 