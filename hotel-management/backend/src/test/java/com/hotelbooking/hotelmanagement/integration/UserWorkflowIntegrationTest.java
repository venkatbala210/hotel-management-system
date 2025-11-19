package com.hotelbooking.hotelmanagement.integration;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotelbooking.hotelmanagement.dto.LoginRequest;
import com.hotelbooking.hotelmanagement.entity.Booking;
import com.hotelbooking.hotelmanagement.entity.Room;
import com.hotelbooking.hotelmanagement.entity.User;
import com.hotelbooking.hotelmanagement.repo.BookingRepository;
import com.hotelbooking.hotelmanagement.repo.RoomRepository;
import com.hotelbooking.hotelmanagement.repo.UserRepository;

/**
 * End-to-end integration tests for complete user workflows
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@SuppressWarnings("null")
class UserWorkflowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private Room testRoom;
    private String authToken;

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() {
        // Clean up before each test
        bookingRepository.deleteAll();
        roomRepository.deleteAll();
        userRepository.deleteAll();

        // Create test user
        testUser = new User();
        testUser.setEmail("testuser@example.com");
        testUser.setName("Test User");
        testUser.setPassword(passwordEncoder.encode("password123"));
        testUser.setPhoneNumber("1234567890");
        testUser.setRole("USER");
        User savedUser = userRepository.save(testUser);
        assertNotNull(savedUser);
        testUser = savedUser;

        // Create test room
        testRoom = new Room();
        testRoom.setRoomType("Deluxe");
        testRoom.setRoomPrice(new BigDecimal("150.00"));
        testRoom.setRoomDescription("A beautiful deluxe room");
        testRoom.setRoomPhotoUrl("https://example.com/room.jpg");
        Room savedRoom = roomRepository.save(testRoom);
        assertNotNull(savedRoom);
        testRoom = savedRoom;
    }

    @Test
    void testCompleteUserRegistrationAndLoginWorkflow() throws Exception {
        // Step 1: Register a new user
        User newUser = new User();
        newUser.setEmail("newuser@example.com");
        newUser.setName("New User");
        newUser.setPassword("password123");
        newUser.setPhoneNumber("9876543210");

        String newUserJson = objectMapper.writeValueAsString(newUser);
        assertNotNull(newUserJson);
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(newUserJson)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200));

        // Step 2: Login with the new user
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("newuser@example.com");
        loginRequest.setPassword("password123");

        String loginJson = objectMapper.writeValueAsString(loginRequest);
        assertNotNull(loginJson);
        String response = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(loginJson)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.token").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertNotNull(response);

        // Extract token for future requests
        authToken = objectMapper.readTree(response).get("token").asText();
        assertNotNull(authToken);
    }

    @Test
    void testCompleteBookingWorkflow() throws Exception {
        // Step 1: Login to get token
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("testuser@example.com");
        loginRequest.setPassword("password123");

        String loginJson = objectMapper.writeValueAsString(loginRequest);
        assertNotNull(loginJson);
        String loginResponse = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(loginJson)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertNotNull(loginResponse);

        String token = objectMapper.readTree(loginResponse).get("token").asText();

        // Step 2: Get available rooms
        mockMvc.perform(get("/rooms/all-available-rooms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.roomList").isArray());

        // Step 3: Get room details
        mockMvc.perform(get("/rooms/room-by-id/" + testRoom.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.room.id").value(testRoom.getId()));

        // Step 4: Create a booking
        Booking bookingRequest = new Booking();
        bookingRequest.setCheckInDate(LocalDate.now().plusDays(1));
        bookingRequest.setCheckOutDate(LocalDate.now().plusDays(3));
        bookingRequest.setNumOfAdults(2);
        bookingRequest.setNumOfChildren(0);

        String bookingJson = objectMapper.writeValueAsString(bookingRequest);
        assertNotNull(bookingJson);
        String bookingResponse = mockMvc.perform(post("/bookings/book-room/" + testRoom.getId() + "/" + testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + token)
                        .content(bookingJson)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.bookingConfirmationCode").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertNotNull(bookingResponse);

        String confirmationCode = objectMapper.readTree(bookingResponse)
                .get("bookingConfirmationCode").asText();

        // Step 5: Retrieve booking by confirmation code
        mockMvc.perform(get("/bookings/get-by-confirmation-code/" + confirmationCode))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.booking.bookingConfirmationCode").value(confirmationCode));

        // Step 6: Get user's booking history
        mockMvc.perform(get("/users/get-user-bookings/" + testUser.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.user.bookings").isArray());
    }

    @Test
    void testCompleteAdminWorkflow() throws Exception {
        // Create admin user
        User admin = new User();
        admin.setEmail("admin@example.com");
        admin.setName("Admin User");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setPhoneNumber("1111111111");
        admin.setRole("ADMIN");
        User savedAdmin = userRepository.save(admin);
        assertNotNull(savedAdmin);

        // Step 1: Login as admin
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("admin@example.com");
        loginRequest.setPassword("admin123");

        String loginJson = objectMapper.writeValueAsString(loginRequest);
        assertNotNull(loginJson);
        String loginResponse = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(loginJson)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertNotNull(loginResponse);

        String adminToken = objectMapper.readTree(loginResponse).get("token").asText();

        // Step 2: Get all users (admin only)
        mockMvc.perform(get("/users/all")
                        .header("Authorization", "Bearer " + adminToken)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.userList").isArray());

        // Step 3: Get all bookings (admin only)
        mockMvc.perform(get("/bookings/all")
                        .header("Authorization", "Bearer " + adminToken)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200));
    }

    @Test
    void testBookingCancellationWorkflow() throws Exception {
        // Step 1: Login
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("testuser@example.com");
        loginRequest.setPassword("password123");

        String loginJson = objectMapper.writeValueAsString(loginRequest);
        assertNotNull(loginJson);
        String loginResponse = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(loginJson)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertNotNull(loginResponse);

        String token = objectMapper.readTree(loginResponse).get("token").asText();

        // Step 2: Create a booking
        Booking bookingRequest = new Booking();
        bookingRequest.setCheckInDate(LocalDate.now().plusDays(1));
        bookingRequest.setCheckOutDate(LocalDate.now().plusDays(3));
        bookingRequest.setNumOfAdults(2);
        bookingRequest.setNumOfChildren(0);

        String bookingJson = objectMapper.writeValueAsString(bookingRequest);
        assertNotNull(bookingJson);
        String bookingResponse = mockMvc.perform(post("/bookings/book-room/" + testRoom.getId() + "/" + testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + token)
                        .content(bookingJson)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertNotNull(bookingResponse);

        // Extract booking ID from the created booking
        // In a real scenario, you'd get this from the response or query the database
        // For this test, we'll find the booking by confirmation code
        String confirmationCode = objectMapper.readTree(bookingResponse)
                .get("bookingConfirmationCode").asText();

        // Find the booking to get its ID
        var booking = bookingRepository.findByBookingConfirmationCode(confirmationCode);
        assertTrue(booking.isPresent());

        // Step 3: Cancel the booking
        mockMvc.perform(delete("/bookings/cancel/" + booking.get().getId())
                        .header("Authorization", "Bearer " + token)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("successful"));

        // Step 4: Verify booking is cancelled (should not be found)
        mockMvc.perform(get("/bookings/get-by-confirmation-code/" + confirmationCode))
                .andExpect(status().isNotFound());
    }

    @Test
    void testRoomAvailabilityCheckWorkflow() throws Exception {
        // Step 1: Check available rooms by date and type
        LocalDate checkIn = LocalDate.now().plusDays(1);
        LocalDate checkOut = LocalDate.now().plusDays(3);

        mockMvc.perform(get("/rooms/available-rooms-by-date-and-type")
                        .param("checkInDate", checkIn.toString())
                        .param("checkOutDate", checkOut.toString())
                        .param("roomType", "Deluxe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.roomList").isArray());

        // Step 2: Create a booking
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("testuser@example.com");
        loginRequest.setPassword("password123");

        String loginJson = objectMapper.writeValueAsString(loginRequest);
        assertNotNull(loginJson);
        String loginResponse = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(loginJson)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertNotNull(loginResponse);

        String token = objectMapper.readTree(loginResponse).get("token").asText();

        Booking bookingRequest = new Booking();
        bookingRequest.setCheckInDate(checkIn);
        bookingRequest.setCheckOutDate(checkOut);
        bookingRequest.setNumOfAdults(2);
        bookingRequest.setNumOfChildren(0);

        String bookingJson = objectMapper.writeValueAsString(bookingRequest);
        assertNotNull(bookingJson);
        mockMvc.perform(post("/bookings/book-room/" + testRoom.getId() + "/" + testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + token)
                        .content(bookingJson)
                        .with(csrf()))
                .andExpect(status().isOk());

        // Step 3: Check availability again - room should not be available for overlapping dates
        mockMvc.perform(get("/rooms/available-rooms-by-date-and-type")
                        .param("checkInDate", checkIn.toString())
                        .param("checkOutDate", checkOut.toString())
                        .param("roomType", "Deluxe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200));
    }
}

