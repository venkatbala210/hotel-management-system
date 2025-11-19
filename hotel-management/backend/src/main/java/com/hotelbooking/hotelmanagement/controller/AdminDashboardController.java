package com.hotelbooking.hotelmanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hotelbooking.hotelmanagement.dto.Response;
import com.hotelbooking.hotelmanagement.service.interfac.IAdminDashboardService;

@RestController
@RequestMapping("/admin/dashboard")
public class AdminDashboardController {

    @Autowired
    private IAdminDashboardService adminDashboardService;

    @GetMapping("/test-auth")
    public ResponseEntity<Response> testAuthentication() {
        Response response = new Response();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth != null) {
            response.setStatusCode(200);
            response.setMessage("Authentication successful. User: " + auth.getName() + 
                ", Authorities: " + auth.getAuthorities().toString());
        } else {
            response.setStatusCode(401);
            response.setMessage("No authentication found");
        }
        
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/statistics")
    public ResponseEntity<Response> getDashboardStatistics() {
        // Debug: Log authentication info BEFORE PreAuthorize check
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("=== DASHBOARD ACCESS DEBUG (BEFORE) ===");
        if (auth != null) {
            System.out.println("Authenticated user: " + auth.getName());
            System.out.println("Authorities: " + auth.getAuthorities());
            System.out.println("Has ADMIN authority: " + auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN")));
            
            // Manual check - if user has ADMIN, proceed
            boolean hasAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN"));
            
            if (!hasAdmin) {
                Response errorResponse = new Response();
                errorResponse.setStatusCode(403);
                errorResponse.setMessage("Access denied. Required authority: ADMIN. Current authorities: " + auth.getAuthorities());
                return ResponseEntity.status(403).body(errorResponse);
            }
        } else {
            System.out.println("No authentication found in SecurityContext");
            Response errorResponse = new Response();
            errorResponse.setStatusCode(401);
            errorResponse.setMessage("No authentication found");
            return ResponseEntity.status(401).body(errorResponse);
        }
        
        Response response = adminDashboardService.getDashboardStatistics();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}

