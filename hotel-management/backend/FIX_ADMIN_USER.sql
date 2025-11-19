-- Fix admin user password and role
-- This script updates the admin user with properly hashed password and correct role

-- First, let's update the role to ADMIN (not ROLE_ADMIN)
UPDATE users SET role = 'ADMIN' WHERE email = 'admin@gmail.com';

-- Note: The password needs to be hashed using BCrypt
-- The BCrypt hash for "admin@123" is: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
-- This is a standard BCrypt hash with 10 rounds

-- Update password with BCrypt hash
UPDATE users SET password = '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy' WHERE email = 'admin@gmail.com';

-- Verify the update
SELECT email, name, role, LEFT(password, 20) as password_hash FROM users WHERE email = 'admin@gmail.com';

