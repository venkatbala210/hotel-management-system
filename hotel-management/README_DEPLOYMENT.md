# Deployment Guide

This guide explains how to deploy the Hotel Management System to Render (backend) and Netlify (frontend).

## Prerequisites

- GitHub account
- Render account (for backend)
- Netlify account (for frontend)
- MySQL database (can use Render's PostgreSQL or external MySQL)

## Backend Deployment (Render)

### Step 1: Prepare Backend for Render

1. Push your code to GitHub
2. Go to [Render Dashboard](https://dashboard.render.com)
3. Click "New +" → "Web Service"
4. Connect your GitHub repository
5. Configure the service:
   - **Name**: `hotel-management-backend`
   - **Environment**: `Java`
   - **Build Command**: `mvn clean package -DskipTests`
   - **Start Command**: `java -jar target/*.jar`
   - **Root Directory**: `hotel-management/backend`

### Step 2: Set Environment Variables in Render

Add these environment variables in Render dashboard:

```
SPRING_DATASOURCE_URL=jdbc:mysql://your-db-host:3306/hotel_booking
SPRING_DATASOURCE_USERNAME=your-db-username
SPRING_DATASOURCE_PASSWORD=your-db-password
SERVER_PORT=5050
AWS_S3_ACCESS_KEY=your-s3-key (or dummy)
AWS_S3_SECRET_KEY=your-s3-secret (or dummy)
```

### Step 3: Database Setup

**Option A: Use Render PostgreSQL (Recommended)**
1. Create a PostgreSQL database in Render
2. Update `SPRING_DATASOURCE_URL` to use PostgreSQL connection string
3. Update `pom.xml` to include PostgreSQL driver dependency

**Option B: Use External MySQL**
1. Use a managed MySQL service (AWS RDS, DigitalOcean, etc.)
2. Update connection string in environment variables

## Frontend Deployment (Netlify)

### Step 1: Prepare Frontend for Netlify

1. Push your code to GitHub
2. Go to [Netlify Dashboard](https://app.netlify.com)
3. Click "Add new site" → "Import an existing project"
4. Connect your GitHub repository
5. Configure the build:
   - **Base directory**: `hotel-management/frontend`
   - **Build command**: `npm run build`
   - **Publish directory**: `hotel-management/frontend/build`

### Step 2: Set Environment Variables in Netlify

1. Go to Site settings → Environment variables
2. Add:
   ```
   REACT_APP_API_URL=https://your-backend-url.onrender.com
   ```
   Replace `your-backend-url.onrender.com` with your actual Render backend URL

### Step 3: Deploy

Netlify will automatically build and deploy your frontend. The `netlify.toml` file handles routing for React Router.

## Docker Deployment (Local/Production)

### Using Docker Compose

1. Navigate to project root:
   ```bash
   cd hotel-management
   ```

2. Start all services:
   ```bash
   docker-compose up -d
   ```

3. Access:
   - Frontend: http://localhost:3000
   - Backend: http://localhost:5050
   - MySQL: localhost:3306

4. Stop services:
   ```bash
   docker-compose down
   ```

### Build Individual Docker Images

**Backend:**
```bash
cd backend
docker build -t hotel-backend .
docker run -p 5050:5050 hotel-backend
```

**Frontend:**
```bash
cd frontend
docker build -t hotel-frontend .
docker run -p 3000:80 hotel-frontend
```

## Environment Variables Reference

### Backend (.env)
- `SPRING_DATASOURCE_URL`: Database connection URL
- `SPRING_DATASOURCE_USERNAME`: Database username
- `SPRING_DATASOURCE_PASSWORD`: Database password
- `SERVER_PORT`: Server port (default: 5050)
- `AWS_S3_ACCESS_KEY`: AWS S3 access key (optional)
- `AWS_S3_SECRET_KEY`: AWS S3 secret key (optional)

### Frontend (.env)
- `REACT_APP_API_URL`: Backend API URL

## Troubleshooting

### Backend Issues
- **Port already in use**: Change `SERVER_PORT` environment variable
- **Database connection failed**: Verify database credentials and network access
- **Build fails**: Check Java version (requires Java 21)

### Frontend Issues
- **API calls fail**: Verify `REACT_APP_API_URL` is set correctly
- **Routing issues**: Ensure `netlify.toml` redirects are configured
- **Build fails**: Check Node.js version (requires Node 18+)

## Production Checklist

- [ ] Update CORS settings in backend for production domain
- [ ] Set secure database credentials
- [ ] Configure proper AWS S3 credentials (if using)
- [ ] Enable HTTPS/SSL certificates
- [ ] Set up database backups
- [ ] Configure monitoring and logging
- [ ] Update frontend API URL to production backend URL

## Support

For issues, check:
- Render logs: Dashboard → Your Service → Logs
- Netlify logs: Site → Deploys → Click deploy → View logs
- Docker logs: `docker-compose logs [service-name]`

