package com.hotelbooking.hotelmanagement.controller;

import java.time.LocalDate;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotelbooking.hotelmanagement.dto.BookingDTO;
import com.hotelbooking.hotelmanagement.dto.Response;
import com.hotelbooking.hotelmanagement.entity.Booking;
import com.hotelbooking.hotelmanagement.service.interfac.IBookingService;

@WebMvcTest(BookingController.class)
@SuppressWarnings("null")
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IBookingService bookingService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(authorities = "USER")
    void testSaveBooking_Success() throws Exception {
        // Given
        Booking bookingRequest = new Booking();
        bookingRequest.setCheckInDate(LocalDate.now().plusDays(1));
        bookingRequest.setCheckOutDate(LocalDate.now().plusDays(3));
        bookingRequest.setNumOfAdults(2);
        bookingRequest.setNumOfChildren(0);

        Response response = new Response();
        response.setStatusCode(200);
        response.setMessage("successful");
        response.setBookingConfirmationCode("ABC123");

        when(bookingService.saveBooking(anyLong(), anyLong(), any(Booking.class)))
                .thenReturn(response);

        // When & Then
        String bookingJson = objectMapper.writeValueAsString(bookingRequest);
        assertNotNull(bookingJson);
        mockMvc.perform(post("/bookings/book-room/1/1")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(bookingJson)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.bookingConfirmationCode").value("ABC123"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testSaveBooking_AsAdmin() throws Exception {
        // Given
        Booking bookingRequest = new Booking();
        bookingRequest.setCheckInDate(LocalDate.now().plusDays(1));
        bookingRequest.setCheckOutDate(LocalDate.now().plusDays(3));

        Response response = new Response();
        response.setStatusCode(200);
        response.setMessage("successful");
        response.setBookingConfirmationCode("ABC123");

        when(bookingService.saveBooking(anyLong(), anyLong(), any(Booking.class)))
                .thenReturn(response);

        // When & Then
        String bookingJson = objectMapper.writeValueAsString(bookingRequest);
        assertNotNull(bookingJson);
        mockMvc.perform(post("/bookings/book-room/1/1")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(bookingJson)
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testGetAllBookings_Success() throws Exception {
        // Given
        BookingDTO bookingDTO = new BookingDTO();
        bookingDTO.setId(1L);
        bookingDTO.setBookingConfirmationCode("ABC123");

        Response response = new Response();
        response.setStatusCode(200);
        response.setMessage("successful");
        response.setBookingList(Arrays.asList(bookingDTO));

        when(bookingService.getAllBookings()).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/bookings/all")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.bookingList").isArray());
    }

    @Test
    void testGetBookingByConfirmationCode_Success() throws Exception {
        // Given
        BookingDTO bookingDTO = new BookingDTO();
        bookingDTO.setId(1L);
        bookingDTO.setBookingConfirmationCode("ABC123");

        Response response = new Response();
        response.setStatusCode(200);
        response.setMessage("successful");
        response.setBooking(bookingDTO);

        when(bookingService.findBookingByConfirmationCode("ABC123")).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/bookings/get-by-confirmation-code/ABC123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.booking.bookingConfirmationCode").value("ABC123"));
    }

    @Test
    void testGetBookingByConfirmationCode_NotFound() throws Exception {
        // Given
        Response response = new Response();
        response.setStatusCode(404);
        response.setMessage("Booking Not Found");

        when(bookingService.findBookingByConfirmationCode("INVALID")).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/bookings/get-by-confirmation-code/INVALID"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(404));
    }

    @Test
    @WithMockUser(authorities = "USER")
    void testCancelBooking_Success() throws Exception {
        // Given
        Response response = new Response();
        response.setStatusCode(200);
        response.setMessage("successful");

        when(bookingService.cancelBooking(1L)).thenReturn(response);

        // When & Then
        mockMvc.perform(delete("/bookings/cancel/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testCancelBooking_AsAdmin() throws Exception {
        // Given
        Response response = new Response();
        response.setStatusCode(200);
        response.setMessage("successful");

        when(bookingService.cancelBooking(1L)).thenReturn(response);

        // When & Then
        mockMvc.perform(delete("/bookings/cancel/1")
                        .with(csrf()))
                .andExpect(status().isOk());
    }
}

