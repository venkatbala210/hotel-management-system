@echo off
echo Starting Hotel Management Backend...
echo.
echo Make sure MySQL is running on localhost:3306
echo Database: hotel_booking
echo Username: root
echo Password: 123456789
echo.
cd backend
echo Using Maven directly...
mvn spring-boot:run
pause

