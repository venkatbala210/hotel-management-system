package com.hotelbooking.hotelmanagement.security;


import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.hotelbooking.hotelmanagement.service.CustomUserDetailsService;
import com.hotelbooking.hotelmanagement.utils.JWTUtils;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JWTAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JWTUtils jwtUtils;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;


    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwtToken;
        final String userEmail;

        if (authHeader == null || authHeader.isBlank() || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            jwtToken = authHeader.substring(7);
            
            if (jwtToken == null || jwtToken.isBlank()) {
                filterChain.doFilter(request, response);
                return;
            }

            userEmail = jwtUtils.extractUsername(jwtToken);

            if (userEmail != null) {
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(userEmail);
                if (jwtUtils.isValidToken(jwtToken, userDetails)) {
                    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
                    UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                        userDetails, 
                        null, 
                        userDetails.getAuthorities()
                    );
                    token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    securityContext.setAuthentication(token);
                    SecurityContextHolder.setContext(securityContext);
                    
                    // Debug logging - always log for admin dashboard debugging
                    System.out.println("=== JWT FILTER DEBUG ===");
                    System.out.println("User: " + userEmail);
                    System.out.println("Authorities: " + userDetails.getAuthorities());
                    System.out.println("Role from UserDetails: " + ((com.hotelbooking.hotelmanagement.entity.User) userDetails).getRole());
                } else {
                    logger.warn("Invalid JWT token for user: " + userEmail);
                    System.out.println("JWT token validation failed for user: " + userEmail);
                }
            }
        } catch (ExpiredJwtException e) {
            logger.warn("JWT token expired: " + e.getMessage(), e);
            // Don't set response here - let Spring Security handle it
        } catch (SignatureException | MalformedJwtException e) {
            logger.warn("Invalid JWT token: " + e.getMessage(), e);
            // Don't set response here - let Spring Security handle it
        } catch (RuntimeException e) {
            logger.error("Error processing JWT token: " + e.getMessage(), e);
            // Continue with filter chain - let Spring Security handle it
        }
        
        filterChain.doFilter(request, response);
    }
}

