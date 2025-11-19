package com.hotelbooking.hotelmanagement.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotelbooking.hotelmanagement.dto.LoginRequest;
import com.hotelbooking.hotelmanagement.dto.Response;
import com.hotelbooking.hotelmanagement.entity.User;
import com.hotelbooking.hotelmanagement.service.interfac.IUserService;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IUserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testRegister_Success() throws Exception {
        // Given
        User user = new User();
        user.setEmail("test@example.com");
        user.setName("Test User");
        user.setPassword("password123");
        user.setPhoneNumber("1234567890");

        Response response = new Response();
        response.setStatusCode(200);
        response.setMessage("successful");

        when(userService.register(any(User.class))).thenReturn(response);

        // When & Then
        String userJson = objectMapper.writeValueAsString(user);
        assertNotNull(userJson);
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(userJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("successful"));
    }

    @Test
    void testRegister_EmailExists() throws Exception {
        // Given
        User user = new User();
        user.setEmail("existing@example.com");
        user.setName("Test User");
        user.setPassword("password123");
        user.setPhoneNumber("1234567890");

        Response response = new Response();
        response.setStatusCode(400);
        response.setMessage("existing@example.comAlready Exists");

        when(userService.register(any(User.class))).thenReturn(response);

        // When & Then
        String userJson = objectMapper.writeValueAsString(user);
        assertNotNull(userJson);
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(userJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400));
    }

    @Test
    void testLogin_Success() throws Exception {
        // Given
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        Response response = new Response();
        response.setStatusCode(200);
        response.setToken("testToken");
        response.setRole("USER");
        response.setMessage("successful");

        when(userService.login(any(LoginRequest.class))).thenReturn(response);

        // When & Then
        String loginJson = objectMapper.writeValueAsString(loginRequest);
        assertNotNull(loginJson);
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.token").value("testToken"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    void testLogin_InvalidCredentials() throws Exception {
        // Given
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("wrongPassword");

        Response response = new Response();
        response.setStatusCode(401);
        response.setMessage("Invalid credentials");

        when(userService.login(any(LoginRequest.class))).thenReturn(response);

        // When & Then
        String loginJson = objectMapper.writeValueAsString(loginRequest);
        assertNotNull(loginJson);
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(loginJson))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.statusCode").value(401))
                .andExpect(jsonPath("$.message").value("Invalid credentials"));
    }
}

