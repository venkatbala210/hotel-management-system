package com.hotelbooking.hotelmanagement.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.hotelbooking.hotelmanagement.entity.User;
import com.hotelbooking.hotelmanagement.repo.UserRepository;

@Component
public class AdminInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Check if admin user already exists
        var existingAdmin = userRepository.findByEmail("adminhotel@gmail.com");
        
        if (existingAdmin.isEmpty()) {
            // Create admin user
            User admin = new User();
            admin.setEmail("adminhotel@gmail.com");
            admin.setName("Admin");
            admin.setPhoneNumber("0000000000");
            admin.setPassword(passwordEncoder.encode("adminhotel@123"));
            admin.setRole("ADMIN");
            
            userRepository.save(admin);
            System.out.println("Admin user created successfully!");
        } else {
            // Update existing admin user to ensure correct password hash and role
            User admin = existingAdmin.get();
            String plainPassword = "adminhotel@123".trim(); // Ensure no whitespace
            
            // Always regenerate password hash to ensure it matches the encoder
            String newHash = passwordEncoder.encode(plainPassword);
            admin.setPassword(newHash); // DO NOT trim BCrypt hash - it will break verification
            System.out.println("Admin user password has been updated with BCrypt hash.");
            System.out.println("Plain password: '" + plainPassword + "'");
            System.out.println("New hash (first 30 chars): " + newHash.substring(0, Math.min(30, newHash.length())));
            System.out.println("Hash length: " + newHash.length());
            System.out.println("Hash starts with $2a$: " + newHash.startsWith("$2a$"));
            
            // Verify the hash works immediately after creation
            boolean testMatch = passwordEncoder.matches(plainPassword, newHash);
            System.out.println("Password hash verification test (immediate): " + testMatch);
            
            // Save and verify again from database
            userRepository.save(admin);
            User savedAdmin = userRepository.findByEmail("adminhotel@gmail.com").orElse(null);
            if (savedAdmin != null) {
                String savedHash = savedAdmin.getPassword();
                boolean savedMatch = passwordEncoder.matches(plainPassword, savedHash);
                System.out.println("Password hash verification test (from DB): " + savedMatch);
                System.out.println("Saved hash (first 30 chars): " + (savedHash != null && savedHash.length() > 30 ? savedHash.substring(0, 30) : savedHash));
            }
            
            // Ensure role is ADMIN (not ROLE_ADMIN)
            if (!"ADMIN".equals(admin.getRole())) {
                admin.setRole("ADMIN");
                System.out.println("Admin user role has been updated to ADMIN.");
            }
            
            userRepository.save(admin);
            System.out.println("Admin user verified and updated successfully!");
        }
    }
}

