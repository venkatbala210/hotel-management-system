package com.hotelbooking.hotelmanagement.controller;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hotelbooking.hotelmanagement.dto.Response;
import com.hotelbooking.hotelmanagement.dto.UserDTO;
import com.hotelbooking.hotelmanagement.service.interfac.IUserService;

@WebMvcTest(UserController.class)
@SuppressWarnings("null")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IUserService userService;

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testGetAllUsers_Success() throws Exception {
        // Given
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setEmail("test@example.com");
        userDTO.setName("Test User");

        Response response = new Response();
        response.setStatusCode(200);
        response.setMessage("successful");
        response.setUserList(Arrays.asList(userDTO));

        when(userService.getAllUsers()).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/users/all")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.userList").isArray())
                .andExpect(jsonPath("$.userList[0].email").value("test@example.com"));
    }

    @Test
    @WithMockUser(authorities = "USER")
    void testGetAllUsers_Forbidden() throws Exception {
        // When & Then - USER role should not access ADMIN endpoint
        mockMvc.perform(get("/users/all")
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetUserById_Success() throws Exception {
        // Given
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setEmail("test@example.com");
        userDTO.setName("Test User");

        Response response = new Response();
        response.setStatusCode(200);
        response.setMessage("successful");
        response.setUser(userDTO);

        when(userService.getUserById("1")).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/users/get-by-id/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.user.email").value("test@example.com"));
    }

    @Test
    void testGetUserById_NotFound() throws Exception {
        // Given
        Response response = new Response();
        response.setStatusCode(404);
        response.setMessage("User Not Found");

        when(userService.getUserById("999")).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/users/get-by-id/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(404));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void testGetLoggedInUserProfile_Success() throws Exception {
        // Given
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setEmail("test@example.com");
        userDTO.setName("Test User");

        Response response = new Response();
        response.setStatusCode(200);
        response.setMessage("successful");
        response.setUser(userDTO);

        when(userService.getMyInfo("test@example.com")).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/users/get-logged-in-profile-info")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.user.email").value("test@example.com"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testDeleteUser_Success() throws Exception {
        // Given
        Response response = new Response();
        response.setStatusCode(200);
        response.setMessage("successful");

        when(userService.deleteUser("1")).thenReturn(response);

        // When & Then
        mockMvc.perform(delete("/users/delete/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200));
    }

    @Test
    void testGetUserBookingHistory_Success() throws Exception {
        // Given
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setEmail("test@example.com");

        Response response = new Response();
        response.setStatusCode(200);
        response.setMessage("successful");
        response.setUser(userDTO);

        when(userService.getUserBookingHistory("1")).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/users/get-user-bookings/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200));
    }
}

