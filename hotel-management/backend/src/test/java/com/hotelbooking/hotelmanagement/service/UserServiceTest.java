package com.hotelbooking.hotelmanagement.service;

import com.hotelbooking.hotelmanagement.dto.LoginRequest;
import com.hotelbooking.hotelmanagement.dto.Response;
import com.hotelbooking.hotelmanagement.dto.UserDTO;
import com.hotelbooking.hotelmanagement.entity.User;
import com.hotelbooking.hotelmanagement.repo.UserRepository;
import com.hotelbooking.hotelmanagement.service.impl.UserService;
import com.hotelbooking.hotelmanagement.utils.JWTUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JWTUtils jwtUtils;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UserDTO testUserDTO;

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setName("Test User");
        testUser.setPhoneNumber("1234567890");
        testUser.setPassword("password123");
        testUser.setRole("USER");

        testUserDTO = new UserDTO();
        testUserDTO.setId(1L);
        testUserDTO.setEmail("test@example.com");
        testUserDTO.setName("Test User");
        testUserDTO.setPhoneNumber("1234567890");
        testUserDTO.setRole("USER");
    }

    @Test
    void testRegister_Success() {
        // Given
        when(userRepository.existsByEmail(testUser.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(testUser.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        Response response = userService.register(testUser);

        // Then
        assertEquals(200, response.getStatusCode());
        assertNotNull(response.getUser());
        verify(userRepository, times(1)).existsByEmail(testUser.getEmail());
        verify(passwordEncoder, times(1)).encode(testUser.getPassword());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegister_EmailAlreadyExists() {
        // Given
        when(userRepository.existsByEmail(testUser.getEmail())).thenReturn(true);

        // When
        Response response = userService.register(testUser);

        // Then
        assertEquals(400, response.getStatusCode());
        assertTrue(response.getMessage().contains("Already Exists"));
        verify(userRepository, times(1)).existsByEmail(testUser.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRegister_DefaultRoleSet() {
        // Given
        testUser.setRole(null);
        when(userRepository.existsByEmail(testUser.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(testUser.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User saved = invocation.getArgument(0);
            assertNotNull(saved);
            saved.setId(1L);
            return saved;
        });

        // When
        Response response = userService.register(testUser);

        // Then
        assertEquals(200, response.getStatusCode());
        verify(userRepository, times(1)).save(argThat(user -> "USER".equals(user.getRole())));
    }

    @Test
    void testRegister_DatabaseError() {
        // Given
        when(userRepository.existsByEmail(testUser.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(testUser.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenThrow(new DataAccessException("DB Error") {});

        // When
        Response response = userService.register(testUser);

        // Then
        assertEquals(500, response.getStatusCode());
        assertTrue(response.getMessage().contains("Database error"));
    }

    @Test
    void testLogin_Success() {
        // Given
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(testUser));
        when(jwtUtils.generateToken(testUser)).thenReturn("testToken");

        // When
        Response response = userService.login(loginRequest);

        // Then
        assertEquals(200, response.getStatusCode());
        assertEquals("testToken", response.getToken());
        assertEquals("USER", response.getRole());
        assertEquals("successful", response.getMessage());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtils, times(1)).generateToken(testUser);
    }

    @Test
    void testLogin_InvalidCredentials() {
        // Given
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("wrongPassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // When
        Response response = userService.login(loginRequest);

        // Then
        assertEquals(401, response.getStatusCode());
        assertEquals("Invalid credentials", response.getMessage());
    }

    @Test
    void testLogin_UserNotFound() {
        // Given
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("nonexistent@example.com");
        loginRequest.setPassword("password123");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.empty());

        // When
        Response response = userService.login(loginRequest);

        // Then
        assertEquals(404, response.getStatusCode());
        assertEquals("user Not found", response.getMessage());
    }

    @Test
    void testGetAllUsers_Success() {
        // Given
        List<User> users = Arrays.asList(testUser);
        when(userRepository.findAll()).thenReturn(users);

        // When
        Response response = userService.getAllUsers();

        // Then
        assertEquals(200, response.getStatusCode());
        assertNotNull(response.getUserList());
        assertEquals("successful", response.getMessage());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testGetUserById_Success() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When
        Response response = userService.getUserById("1");

        // Then
        assertEquals(200, response.getStatusCode());
        assertNotNull(response.getUser());
        assertEquals("successful", response.getMessage());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testGetUserById_NotFound() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        Response response = userService.getUserById("1");

        // Then
        assertEquals(404, response.getStatusCode());
        assertEquals("User Not Found", response.getMessage());
    }

    @Test
    void testGetUserById_InvalidId() {
        // When
        Response response = userService.getUserById("invalid");

        // Then
        assertEquals(404, response.getStatusCode());
        assertEquals("Invalid user identifier", response.getMessage());
    }

    @Test
    void testGetMyInfo_Success() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // When
        Response response = userService.getMyInfo("test@example.com");

        // Then
        assertEquals(200, response.getStatusCode());
        assertNotNull(response.getUser());
        assertEquals("successful", response.getMessage());
        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    void testDeleteUser_Success() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        doNothing().when(userRepository).deleteById(1L);

        // When
        Response response = userService.deleteUser("1");

        // Then
        assertEquals(200, response.getStatusCode());
        assertEquals("successful", response.getMessage());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteUser_NotFound() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        Response response = userService.deleteUser("1");

        // Then
        assertEquals(404, response.getStatusCode());
        assertEquals("User Not Found", response.getMessage());
        verify(userRepository, never()).deleteById(anyLong());
    }

    @Test
    void testGetUserBookingHistory_Success() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When
        Response response = userService.getUserBookingHistory("1");

        // Then
        assertEquals(200, response.getStatusCode());
        assertNotNull(response.getUser());
        assertEquals("successful", response.getMessage());
        verify(userRepository, times(1)).findById(1L);
    }
}

