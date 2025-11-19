-- SQL script to add admin user to the hotel_booking database
-- 
-- IMPORTANT: The recommended approach is to use the Spring Boot AdminInitializer 
-- component which will automatically create the admin user on startup.
-- Just restart the backend server and the admin user will be created automatically.
--
-- Admin Credentials:
-- Email: admin@gmail.com
-- Password: admin@123
-- Role: ADMIN
--
-- If you need to add manually via SQL, you must first generate a BCrypt hash:
-- 1. Run: mvn compile exec:java -Dexec.mainClass="com.hotelbooking.hotelmanagement.util.PasswordHashGenerator"
-- 2. Copy the generated BCrypt hash
-- 3. Replace the password hash in the INSERT statement below

USE hotel_booking;

-- Manual insertion (requires BCrypt hash - see instructions above)
-- INSERT INTO users (email, name, phone_number, password, role)
-- SELECT 
--     'admin@gmail.com',
--     'Admin',
--     '0000000000',
--     'YOUR_BCRYPT_HASH_HERE', -- Replace with generated BCrypt hash
--     'ADMIN'
-- WHERE NOT EXISTS (
--     SELECT 1 FROM users WHERE email = 'admin@gmail.com'
-- );

-- Verify the admin user exists
SELECT * FROM users WHERE email = 'admin@gmail.com';

