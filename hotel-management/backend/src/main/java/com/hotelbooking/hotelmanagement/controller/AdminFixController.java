package com.hotelbooking.hotelmanagement.controller;

import com.hotelbooking.hotelmanagement.entity.User;
import com.hotelbooking.hotelmanagement.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin/fix")
public class AdminFixController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/password")
    public ResponseEntity<Map<String, Object>> fixAdminPassword() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            var adminOpt = userRepository.findByEmail("admin@gmail.com");
            if (adminOpt.isPresent()) {
                User admin = adminOpt.get();
                
                // Check if password needs hashing
                if (admin.getPassword() == null || !admin.getPassword().startsWith("$2a$")) {
                    admin.setPassword(passwordEncoder.encode("admin@123"));
                    admin.setRole("ADMIN");
                    userRepository.save(admin);
                    
                    response.put("status", "success");
                    response.put("message", "Admin password has been hashed and role updated to ADMIN");
                } else {
                    response.put("status", "already_fixed");
                    response.put("message", "Admin password is already hashed");
                }
                
                response.put("email", admin.getEmail());
                response.put("role", admin.getRole());
                response.put("passwordHashed", admin.getPassword().startsWith("$2a$"));
                
            } else {
                response.put("status", "error");
                response.put("message", "Admin user not found");
            }
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error fixing admin: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
}

