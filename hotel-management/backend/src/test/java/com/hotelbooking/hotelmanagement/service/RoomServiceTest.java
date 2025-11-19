package com.hotelbooking.hotelmanagement.service;

import com.hotelbooking.hotelmanagement.dto.Response;
import com.hotelbooking.hotelmanagement.entity.Room;
import com.hotelbooking.hotelmanagement.repo.RoomRepository;
import com.hotelbooking.hotelmanagement.service.impl.RoomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private AwsS3Service awsS3Service;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private RoomService roomService;

    private Room testRoom;

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() {
        testRoom = new Room();
        testRoom.setId(1L);
        testRoom.setRoomType("Deluxe");
        testRoom.setRoomPrice(new BigDecimal("150.00"));
        testRoom.setRoomDescription("A beautiful deluxe room");
        testRoom.setRoomPhotoUrl("https://example.com/room.jpg");
    }

    @Test
    void testAddNewRoom_Success() {
        // Given
        when(multipartFile.isEmpty()).thenReturn(false);
        when(awsS3Service.saveImageToS3(any(MultipartFile.class))).thenReturn("https://example.com/room.jpg");
        when(roomRepository.save(any(Room.class))).thenReturn(testRoom);

        // When
        Response response = roomService.addNewRoom(
                multipartFile,
                "Deluxe",
                new BigDecimal("150.00"),
                "A beautiful deluxe room"
        );

        // Then
        assertEquals(200, response.getStatusCode());
        assertNotNull(response.getRoom());
        assertEquals("successful", response.getMessage());
        verify(awsS3Service, times(1)).saveImageToS3(any(MultipartFile.class));
        verify(roomRepository, times(1)).save(any(Room.class));
    }

    @Test
    void testAddNewRoom_S3Error() {
        // Given
        when(multipartFile.isEmpty()).thenReturn(false);
        when(awsS3Service.saveImageToS3(any(MultipartFile.class)))
                .thenThrow(new RuntimeException("S3 upload failed"));

        // When
        Response response = roomService.addNewRoom(
                multipartFile,
                "Deluxe",
                new BigDecimal("150.00"),
                "A beautiful deluxe room"
        );

        // Then
        assertEquals(500, response.getStatusCode());
        assertTrue(response.getMessage().contains("Error saving a room"));
    }

    @Test
    void testGetAllRoomTypes_Success() {
        // Given
        List<String> roomTypes = Arrays.asList("Deluxe", "Standard", "Suite");
        when(roomRepository.findDistinctRoomTypes()).thenReturn(roomTypes);

        // When
        List<String> result = roomService.getAllRoomTypes();

        // Then
        assertNotNull(result);
        assertEquals(3, result.size());
        verify(roomRepository, times(1)).findDistinctRoomTypes();
    }

    @Test
    void testGetAllRooms_Success() {
        // Given
        List<Room> rooms = Arrays.asList(testRoom);
        when(roomRepository.findAll(any(Sort.class))).thenReturn(rooms);

        // When
        Response response = roomService.getAllRooms();

        // Then
        assertEquals(200, response.getStatusCode());
        assertNotNull(response.getRoomList());
        assertEquals(1, response.getRoomList().size());
        assertEquals("successful", response.getMessage());
        verify(roomRepository, times(1)).findAll(any(Sort.class));
    }

    @Test
    void testGetRoomById_Success() {
        // Given
        when(roomRepository.findById(1L)).thenReturn(Optional.of(testRoom));

        // When
        Response response = roomService.getRoomById(1L);

        // Then
        assertEquals(200, response.getStatusCode());
        assertNotNull(response.getRoom());
        assertEquals("successful", response.getMessage());
        verify(roomRepository, times(1)).findById(1L);
    }

    @Test
    void testGetRoomById_NotFound() {
        // Given
        when(roomRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        Response response = roomService.getRoomById(1L);

        // Then
        assertEquals(404, response.getStatusCode());
        assertEquals("Room Not Found", response.getMessage());
    }

    @Test
    void testDeleteRoom_Success() {
        // Given
        when(roomRepository.findById(1L)).thenReturn(Optional.of(testRoom));
        doNothing().when(roomRepository).deleteById(1L);

        // When
        Response response = roomService.deleteRoom(1L);

        // Then
        assertEquals(200, response.getStatusCode());
        assertEquals("successful", response.getMessage());
        verify(roomRepository, times(1)).findById(1L);
        verify(roomRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteRoom_NotFound() {
        // Given
        when(roomRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        Response response = roomService.deleteRoom(1L);

        // Then
        assertEquals(404, response.getStatusCode());
        assertEquals("Room Not Found", response.getMessage());
        verify(roomRepository, never()).deleteById(anyLong());
    }

    @Test
    void testUpdateRoom_Success() {
        // Given
        when(roomRepository.findById(1L)).thenReturn(Optional.of(testRoom));
        when(awsS3Service.saveImageToS3(any(MultipartFile.class))).thenReturn("https://example.com/new-room.jpg");
        when(multipartFile.isEmpty()).thenReturn(false);
        when(roomRepository.save(any(Room.class))).thenReturn(testRoom);

        // When
        Response response = roomService.updateRoom(
                1L,
                "Updated description",
                "Suite",
                new BigDecimal("200.00"),
                multipartFile
        );

        // Then
        assertEquals(200, response.getStatusCode());
        assertNotNull(response.getRoom());
        assertEquals("successful", response.getMessage());
        verify(roomRepository, times(1)).findById(1L);
        verify(roomRepository, times(1)).save(any(Room.class));
    }

    @Test
    void testUpdateRoom_WithoutPhoto() {
        // Given
        when(roomRepository.findById(1L)).thenReturn(Optional.of(testRoom));
        when(roomRepository.save(any(Room.class))).thenReturn(testRoom);

        // When
        Response response = roomService.updateRoom(
                1L,
                "Updated description",
                "Suite",
                new BigDecimal("200.00"),
                null
        );

        // Then
        assertEquals(200, response.getStatusCode());
        verify(awsS3Service, never()).saveImageToS3(any());
        verify(roomRepository, times(1)).save(any(Room.class));
    }

    @Test
    void testUpdateRoom_NotFound() {
        // Given
        when(roomRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        Response response = roomService.updateRoom(
                1L,
                "Updated description",
                "Suite",
                new BigDecimal("200.00"),
                null
        );

        // Then
        assertEquals(404, response.getStatusCode());
        assertEquals("Room Not Found", response.getMessage());
        verify(roomRepository, never()).save(any(Room.class));
    }

    @Test
    void testGetAvailableRoomsByDataAndType_Success() {
        // Given
        LocalDate checkIn = LocalDate.now().plusDays(1);
        LocalDate checkOut = LocalDate.now().plusDays(3);
        List<Room> availableRooms = Arrays.asList(testRoom);
        when(roomRepository.findAvailableRoomsByDatesAndTypes(checkIn, checkOut, "Deluxe"))
                .thenReturn(availableRooms);

        // When
        Response response = roomService.getAvailableRoomsByDataAndType(checkIn, checkOut, "Deluxe");

        // Then
        assertEquals(200, response.getStatusCode());
        assertNotNull(response.getRoomList());
        assertEquals("successful", response.getMessage());
        verify(roomRepository, times(1))
                .findAvailableRoomsByDatesAndTypes(checkIn, checkOut, "Deluxe");
    }

    @Test
    void testGetAllAvailableRooms_Success() {
        // Given
        List<Room> availableRooms = Arrays.asList(testRoom);
        when(roomRepository.getAllAvailableRooms()).thenReturn(availableRooms);

        // When
        Response response = roomService.getAllAvailableRooms();

        // Then
        assertEquals(200, response.getStatusCode());
        assertNotNull(response.getRoomList());
        assertEquals("successful", response.getMessage());
        verify(roomRepository, times(1)).getAllAvailableRooms();
    }
}

