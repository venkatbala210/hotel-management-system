# Hotel Management System - Errors and Solutions

## Current Issues Found

### 1. **ERR_CONNECTION_REFUSED Error**
**Problem:** Browser shows "localhost refused to connect"

**Root Causes:**
- Backend server is not running
- MySQL database is not running or not configured
- Port conflicts

### 2. **Maven Wrapper Issue**
**Problem:** Maven wrapper (mvnw.cmd) is incomplete - missing `.mvn/wrapper` directory

**Solution Options:**
- Use Maven directly if installed: `mvn spring-boot:run`
- Or regenerate Maven wrapper

### 3. **Port Configuration**
- **Backend:** Port 5050 (configured in `application.properties`)
- **Frontend:** Port 7070 (configured in `package.json`)
- **API Calls:** Frontend correctly points to `http://localhost:5050`

## How to Start the Application

### Prerequisites
1. **Java 21** must be installed
2. **Maven** must be installed (or use Maven wrapper)
3. **MySQL** must be running with:
   - Database: `hotel_booking`
   - Username: `root`
   - Password: `123456789`
   - Port: `3306`

### Step 1: Start MySQL
Make sure MySQL is running and the database exists:
```sql
CREATE DATABASE IF NOT EXISTS hotel_booking;
```

### Step 2: Start Backend
**Option A - Using Maven directly:**
```bash
cd hotel-management/backend
mvn spring-boot:run
```

**Option B - Using startup script:**
Double-click `START_BACKEND.bat`

**Expected Output:**
```
Started HotelManagementApplication in X.XXX seconds
```

### Step 3: Start Frontend
**Option A - Using npm:**
```bash
cd hotel-management/frontend
npm install  # First time only
npm start
```

**Option B - Using startup script:**
Double-click `START_FRONTEND.bat`

**Expected Output:**
```
Compiled successfully!
You can now view hotel-booking in the browser.
  Local:            http://localhost:7070
```

## Troubleshooting

### Backend Won't Start
1. **Check MySQL Connection:**
   - Verify MySQL is running: `mysql -u root -p`
   - Check if database exists: `SHOW DATABASES;`
   - Create database if missing: `CREATE DATABASE hotel_booking;`

2. **Check Java Version:**
   ```bash
   java -version  # Should be Java 21
   ```

3. **Check Port 5050:**
   ```bash
   netstat -ano | findstr :5050
   ```
   If port is in use, change `server.port` in `application.properties`

### Frontend Won't Connect to Backend
1. **Verify Backend is Running:**
   - Open browser: `http://localhost:5050`
   - Should see some response (even if error, means server is up)

2. **Check API Service Configuration:**
   - File: `frontend/src/service/ApiService.js`
   - Line 5: `static BASE_URL = "http://localhost:5050"`

3. **Check CORS Configuration:**
   - Backend should allow requests from `http://localhost:7070`

### Common Error Messages

**"Cannot connect to MySQL"**
- Start MySQL service
- Verify credentials in `application.properties`
- Check if database exists

**"Port already in use"**
- Find process using port: `netstat -ano | findstr :5050`
- Kill process or change port in `application.properties`

**"Maven wrapper not found"**
- Install Maven: https://maven.apache.org/download.cgi
- Or use: `mvn spring-boot:run` directly

## Quick Start Commands

**Terminal 1 (Backend):**
```bash
cd hotel-management/backend
mvn spring-boot:run
```

**Terminal 2 (Frontend):**
```bash
cd hotel-management/frontend
npm start
```

**Then open:** http://localhost:7070

