package com.hotelbooking.hotelmanagement.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.hotelbooking.hotelmanagement.dto.LoginRequest;
import com.hotelbooking.hotelmanagement.dto.Response;
import com.hotelbooking.hotelmanagement.dto.UserDTO;
import com.hotelbooking.hotelmanagement.entity.User;
import com.hotelbooking.hotelmanagement.exception.OurException;
import com.hotelbooking.hotelmanagement.repo.UserRepository;
import com.hotelbooking.hotelmanagement.service.interfac.IUserService;
import com.hotelbooking.hotelmanagement.utils.JWTUtils;
import com.hotelbooking.hotelmanagement.utils.Utils;

@Service
public class UserService implements IUserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JWTUtils jwtUtils;
    @Override
    public Response register(User user) {
        Response response = new Response();
        try {
            if (user.getRole() == null || user.getRole().isBlank()) {
                user.setRole("USER");
            }
            if (userRepository.existsByEmail(user.getEmail())) {
                throw new OurException(user.getEmail() + "Already Exists");
            }
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            User savedUser = userRepository.save(user);
            UserDTO userDTO = Utils.mapUserEntityToUserDTO(savedUser);
            response.setStatusCode(200);
            response.setUser(userDTO);
        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (DataAccessException e) {
            response.setStatusCode(500);
            response.setMessage("Database error during user registration " + e.getMessage());
            log.error("Database error during registration for {}", user.getEmail(), e);
        } catch (RuntimeException e) {
            response.setStatusCode(500);
            response.setMessage("Error Occurred During USer Registration " + e.getMessage());
            log.error("Unexpected error during registration for {}", user.getEmail(), e);
        }
        return response;
    }

    @Override
    public Response login(LoginRequest loginRequest) {

        Response response = new Response();

        try {
            var user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new OurException("user Not found"));

            // Trim input password to remove any whitespace (but NOT the stored hash)
            String inputPassword = loginRequest.getPassword() != null ? loginRequest.getPassword().trim() : "";
            String storedPassword = user.getPassword(); // DO NOT trim BCrypt hash - it will break verification
            
            log.info("Login attempt for email: {}", loginRequest.getEmail());
            log.info("Input password length: {}", inputPassword.length());
            log.info("Stored password hash (first 30 chars): {}", storedPassword.length() > 30 ? storedPassword.substring(0, 30) : storedPassword);
            log.info("Stored password hash length: {}", storedPassword.length());
            log.info("Stored password hash starts with $2a$: {}", storedPassword.startsWith("$2a$"));
            
            if (storedPassword.isEmpty() || !storedPassword.startsWith("$2a$")) {
                log.error("Invalid password hash format for user: {}", loginRequest.getEmail());
                throw new BadCredentialsException("Password hash is invalid. Please contact administrator.");
            }
            
            boolean passwordMatches = passwordEncoder.matches(inputPassword, storedPassword);
            log.info("Password match result for {}: {}", loginRequest.getEmail(), passwordMatches);
            
            if (!passwordMatches) {
                // Test if password encoder works at all
                String testHash = passwordEncoder.encode(inputPassword);
                boolean testMatch = passwordEncoder.matches(inputPassword, testHash);
                log.warn("Password mismatch. Test hash verification: {}", testMatch);
                log.warn("Input password: '{}'", inputPassword);
                log.warn("Expected password should be: 'admin@123'");
                throw new BadCredentialsException("Password mismatch");
            }

            var token = jwtUtils.generateToken(user);
            response.setStatusCode(200);
            response.setToken(token);
            response.setRole(user.getRole());
            response.setExpirationTime("7 Days");
            response.setMessage("successful");

        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());

        } catch (BadCredentialsException e) {
            response.setStatusCode(401);
            response.setMessage(e.getMessage());
            log.warn("Authentication failed for user {}", loginRequest.getEmail());
        } catch (RuntimeException e) {
            response.setStatusCode(500);
            response.setMessage("Error Occurred During USer Login " + e.getMessage());
            log.error("Unexpected error during user login for {}", loginRequest.getEmail(), e);
        }
        return response;
    }

    @Override
    public Response getAllUsers() {

        Response response = new Response();
        try {
            List<User> userList = userRepository.findAll();
            List<UserDTO> userDTOList = Utils.mapUserListEntityToUserListDTO(userList);
            response.setStatusCode(200);
            response.setMessage("successful");
            response.setUserList(userDTOList);

        } catch (DataAccessException e) {
            response.setStatusCode(500);
            response.setMessage("Database error retrieving users " + e.getMessage());
            log.error("Database error retrieving all users", e);
        } catch (RuntimeException e) {
            response.setStatusCode(500);
            response.setMessage("Error getting all users " + e.getMessage());
            log.error("Unexpected error retrieving all users", e);
        }
        return response;
    }

    @Override
    public Response getUserBookingHistory(String userId) {

        Response response = new Response();


        try {
            long resolvedUserId = resolveUserId(userId);
            User user = userRepository.findById(resolvedUserId).orElseThrow(() -> new OurException("User Not Found"));
            UserDTO userDTO = Utils.mapUserEntityToUserDTOPlusUserBookingsAndRoom(user);
            response.setStatusCode(200);
            response.setMessage("successful");
            response.setUser(userDTO);

        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());

        } catch (DataAccessException e) {
            response.setStatusCode(500);
            response.setMessage("Database error retrieving user booking history " + e.getMessage());
            log.error("Database error retrieving booking history for user {}", userId, e);
        } catch (RuntimeException e) {
            response.setStatusCode(500);
            response.setMessage("Error retrieving user booking history " + e.getMessage());
            log.error("Unexpected error retrieving booking history for user {}", userId, e);
        }
        return response;
    }

    @Override
    public Response deleteUser(String userId) {

        Response response = new Response();

        try {
            long resolvedUserId = resolveUserId(userId);
            userRepository.findById(resolvedUserId).orElseThrow(() -> new OurException("User Not Found"));
            userRepository.deleteById(resolvedUserId);
            response.setStatusCode(200);
            response.setMessage("successful");

        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());

        } catch (DataAccessException e) {
            response.setStatusCode(500);
            response.setMessage("Database error deleting user " + e.getMessage());
            log.error("Database error deleting user {}", userId, e);
        } catch (RuntimeException e) {
            response.setStatusCode(500);
            response.setMessage("Error deleting user " + e.getMessage());
            log.error("Unexpected error deleting user {}", userId, e);
        }
        return response;
    }

    @Override
    public Response getUserById(String userId) {

        Response response = new Response();

        try {
            long resolvedUserId = resolveUserId(userId);
            User user = userRepository.findById(resolvedUserId).orElseThrow(() -> new OurException("User Not Found"));
            UserDTO userDTO = Utils.mapUserEntityToUserDTO(user);
            response.setStatusCode(200);
            response.setMessage("successful");
            response.setUser(userDTO);

        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());

        } catch (DataAccessException e) {
            response.setStatusCode(500);
            response.setMessage("Database error retrieving user " + e.getMessage());
            log.error("Database error retrieving user {}", userId, e);
        } catch (RuntimeException e) {
            response.setStatusCode(500);
            response.setMessage("Error retrieving user " + e.getMessage());
            log.error("Unexpected error retrieving user {}", userId, e);
        }
        return response;
    }

    @Override
    public Response getMyInfo(String email) {

        Response response = new Response();

        try {
            User user = userRepository.findByEmail(email).orElseThrow(() -> new OurException("User Not Found"));
            UserDTO userDTO = Utils.mapUserEntityToUserDTO(user);
            response.setStatusCode(200);
            response.setMessage("successful");
            response.setUser(userDTO);

        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());

        } catch (DataAccessException e) {
            response.setStatusCode(500);
            response.setMessage("Database error retrieving user info " + e.getMessage());
            log.error("Database error retrieving info for {}", email, e);
        } catch (RuntimeException e) {
            response.setStatusCode(500);
            response.setMessage("Error retrieving user info " + e.getMessage());
            log.error("Unexpected error retrieving info for {}", email, e);
        }
        return response;
    }

    private long resolveUserId(String userId) {
        try {
            return Long.parseLong(userId);
        } catch (NumberFormatException e) {
            throw new OurException("Invalid user identifier");
        }
    }
}

