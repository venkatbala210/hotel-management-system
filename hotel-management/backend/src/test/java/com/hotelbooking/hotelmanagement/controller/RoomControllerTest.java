package com.hotelbooking.hotelmanagement.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hotelbooking.hotelmanagement.dto.Response;
import com.hotelbooking.hotelmanagement.dto.RoomDTO;
import com.hotelbooking.hotelmanagement.service.interfac.IRoomService;

@WebMvcTest(RoomController.class)
@SuppressWarnings("null")
class RoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IRoomService roomService;

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testAddNewRoom_Success() throws Exception {
        // Given
        MockMultipartFile photo = new MockMultipartFile(
                "photo", "room.jpg", MediaType.IMAGE_JPEG_VALUE, "test image".getBytes());

        RoomDTO roomDTO = new RoomDTO();
        roomDTO.setId(1L);
        roomDTO.setRoomType("Deluxe");
        roomDTO.setRoomPrice(new BigDecimal("150.00"));

        Response response = new Response();
        response.setStatusCode(200);
        response.setMessage("successful");
        response.setRoom(roomDTO);

        when(roomService.addNewRoom(any(), anyString(), any(BigDecimal.class), anyString()))
                .thenReturn(response);

        // When & Then
        mockMvc.perform(multipart("/rooms/add")
                        .file(photo)
                        .param("roomType", "Deluxe")
                        .param("roomPrice", "150.00")
                        .param("roomDescription", "A beautiful room")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testAddNewRoom_MissingFields() throws Exception {
        // When & Then
        mockMvc.perform(multipart("/rooms/add")
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400));
    }

    @Test
    void testGetAllRooms_Success() throws Exception {
        // Given
        RoomDTO roomDTO = new RoomDTO();
        roomDTO.setId(1L);
        roomDTO.setRoomType("Deluxe");

        Response response = new Response();
        response.setStatusCode(200);
        response.setMessage("successful");
        response.setRoomList(Arrays.asList(roomDTO));

        when(roomService.getAllRooms()).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/rooms/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.roomList").isArray());
    }

    @Test
    void testGetRoomTypes_Success() throws Exception {
        // Given
        List<String> roomTypes = Arrays.asList("Deluxe", "Standard", "Suite");
        when(roomService.getAllRoomTypes()).thenReturn(roomTypes);

        // When & Then
        mockMvc.perform(get("/rooms/types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("Deluxe"))
                .andExpect(jsonPath("$[1]").value("Standard"));
    }

    @Test
    void testGetRoomById_Success() throws Exception {
        // Given
        RoomDTO roomDTO = new RoomDTO();
        roomDTO.setId(1L);
        roomDTO.setRoomType("Deluxe");

        Response response = new Response();
        response.setStatusCode(200);
        response.setMessage("successful");
        response.setRoom(roomDTO);

        when(roomService.getRoomById(1L)).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/rooms/room-by-id/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.room.roomType").value("Deluxe"));
    }

    @Test
    void testGetAvailableRooms_Success() throws Exception {
        // Given
        RoomDTO roomDTO = new RoomDTO();
        roomDTO.setId(1L);
        roomDTO.setRoomType("Deluxe");

        Response response = new Response();
        response.setStatusCode(200);
        response.setMessage("successful");
        response.setRoomList(Arrays.asList(roomDTO));

        when(roomService.getAllAvailableRooms()).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/rooms/all-available-rooms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200));
    }

    @Test
    void testGetAvailableRoomsByDateAndType_Success() throws Exception {
        // Given
        RoomDTO roomDTO = new RoomDTO();
        roomDTO.setId(1L);
        roomDTO.setRoomType("Deluxe");

        Response response = new Response();
        response.setStatusCode(200);
        response.setMessage("successful");
        response.setRoomList(Arrays.asList(roomDTO));

        LocalDate checkIn = LocalDate.now().plusDays(1);
        LocalDate checkOut = LocalDate.now().plusDays(3);

        when(roomService.getAvailableRoomsByDataAndType(any(LocalDate.class), any(LocalDate.class), anyString()))
                .thenReturn(response);

        // When & Then
        mockMvc.perform(get("/rooms/available-rooms-by-date-and-type")
                        .param("checkInDate", checkIn.toString())
                        .param("checkOutDate", checkOut.toString())
                        .param("roomType", "Deluxe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200));
    }

    @Test
    void testGetAvailableRoomsByDateAndType_MissingParams() throws Exception {
        // When & Then
        mockMvc.perform(get("/rooms/available-rooms-by-date-and-type"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testUpdateRoom_Success() throws Exception {
        // Given
        MockMultipartFile photo = new MockMultipartFile(
                "photo", "room.jpg", MediaType.IMAGE_JPEG_VALUE, "test image".getBytes());

        RoomDTO roomDTO = new RoomDTO();
        roomDTO.setId(1L);
        roomDTO.setRoomType("Suite");

        Response response = new Response();
        response.setStatusCode(200);
        response.setMessage("successful");
        response.setRoom(roomDTO);

        when(roomService.updateRoom(anyLong(), anyString(), anyString(), any(BigDecimal.class), any()))
                .thenReturn(response);

        // When & Then
        mockMvc.perform(multipart("/rooms/update/1")
                        .file(photo)
                        .param("roomType", "Suite")
                        .param("roomPrice", "200.00")
                        .with(csrf())
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testDeleteRoom_Success() throws Exception {
        // Given
        Response response = new Response();
        response.setStatusCode(200);
        response.setMessage("successful");

        when(roomService.deleteRoom(1L)).thenReturn(response);

        // When & Then
        mockMvc.perform(delete("/rooms/delete/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200));
    }
}

