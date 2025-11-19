# Docker Quick Start Guide

## Prerequisites
- Docker Desktop installed and running
- Docker Compose installed

## Quick Start

### 1. Start All Services
```bash
cd hotel-management
docker-compose up -d
```

This will start:
- MySQL database on port 3306
- Backend API on port 5050
- Frontend on port 3000

### 2. Access the Application
- Frontend: http://localhost:3000
- Backend API: http://localhost:5050
- MySQL: localhost:3306

### 3. Stop Services
```bash
docker-compose down
```

### 4. View Logs
```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f backend
docker-compose logs -f frontend
docker-compose logs -f mysql
```

### 5. Rebuild After Code Changes
```bash
docker-compose up -d --build
```

## Individual Service Commands

### Backend Only
```bash
cd backend
docker build -t hotel-backend .
docker run -p 5050:5050 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/hotel_booking \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=123456789 \
  hotel-backend
```

### Frontend Only
```bash
cd frontend
docker build -t hotel-frontend .
docker run -p 3000:80 \
  -e REACT_APP_API_URL=http://localhost:5050 \
  hotel-frontend
```

## Troubleshooting

### Port Already in Use
If ports 3000, 5050, or 3306 are already in use, edit `docker-compose.yml` to use different ports.

### Database Connection Issues
Ensure MySQL container is healthy:
```bash
docker-compose ps
```

### Clear Everything and Start Fresh
```bash
docker-compose down -v
docker-compose up -d --build
```

