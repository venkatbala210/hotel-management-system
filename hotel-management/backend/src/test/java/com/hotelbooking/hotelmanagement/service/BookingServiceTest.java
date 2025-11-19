package com.hotelbooking.hotelmanagement.service;

import com.hotelbooking.hotelmanagement.dto.Response;
import com.hotelbooking.hotelmanagement.entity.Booking;
import com.hotelbooking.hotelmanagement.entity.Room;
import com.hotelbooking.hotelmanagement.entity.User;
import com.hotelbooking.hotelmanagement.repo.BookingRepository;
import com.hotelbooking.hotelmanagement.repo.RoomRepository;
import com.hotelbooking.hotelmanagement.repo.UserRepository;
import com.hotelbooking.hotelmanagement.service.impl.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookingService bookingService;

    private Room testRoom;
    private User testUser;
    private Booking testBooking;

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() {
        testRoom = new Room();
        testRoom.setId(1L);
        testRoom.setRoomType("Deluxe");
        testRoom.setRoomPrice(new BigDecimal("150.00"));
        testRoom.setBookings(new ArrayList<>());

        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setName("Test User");

        testBooking = new Booking();
        testBooking.setId(1L);
        testBooking.setCheckInDate(LocalDate.now().plusDays(1));
        testBooking.setCheckOutDate(LocalDate.now().plusDays(3));
        testBooking.setNumOfAdults(2);
        testBooking.setNumOfChildren(0);
        testBooking.setRoom(testRoom);
        testBooking.setUser(testUser);
    }

    @Test
    void testSaveBooking_Success() {
        // Given
        LocalDate checkIn = LocalDate.now().plusDays(1);
        LocalDate checkOut = LocalDate.now().plusDays(3);
        Booking bookingRequest = new Booking();
        bookingRequest.setCheckInDate(checkIn);
        bookingRequest.setCheckOutDate(checkOut);
        bookingRequest.setNumOfAdults(2);
        bookingRequest.setNumOfChildren(0);

        when(roomRepository.findById(1L)).thenReturn(Optional.of(testRoom));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking saved = invocation.getArgument(0);
            assertNotNull(saved);
            saved.setId(1L);
            saved.setBookingConfirmationCode("ABC123");
            return saved;
        });

        // When
        Response response = bookingService.saveBooking(1L, 1L, bookingRequest);

        // Then
        assertEquals(200, response.getStatusCode());
        assertNotNull(response.getBookingConfirmationCode());
        assertEquals("successful", response.getMessage());
        verify(roomRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findById(1L);
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void testSaveBooking_InvalidDateRange() {
        // Given
        Booking bookingRequest = new Booking();
        bookingRequest.setCheckInDate(LocalDate.now().plusDays(3));
        bookingRequest.setCheckOutDate(LocalDate.now().plusDays(1)); // Check-out before check-in

        // When
        Response response = bookingService.saveBooking(1L, 1L, bookingRequest);

        // Then
        assertEquals(400, response.getStatusCode());
        assertTrue(response.getMessage().contains("Check in date must come after check out date"));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void testSaveBooking_RoomNotFound() {
        // Given
        Booking bookingRequest = new Booking();
        bookingRequest.setCheckInDate(LocalDate.now().plusDays(1));
        bookingRequest.setCheckOutDate(LocalDate.now().plusDays(3));

        when(roomRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        Response response = bookingService.saveBooking(1L, 1L, bookingRequest);

        // Then
        assertEquals(404, response.getStatusCode());
        assertEquals("Room Not Found", response.getMessage());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void testSaveBooking_UserNotFound() {
        // Given
        Booking bookingRequest = new Booking();
        bookingRequest.setCheckInDate(LocalDate.now().plusDays(1));
        bookingRequest.setCheckOutDate(LocalDate.now().plusDays(3));

        when(roomRepository.findById(1L)).thenReturn(Optional.of(testRoom));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        Response response = bookingService.saveBooking(1L, 1L, bookingRequest);

        // Then
        assertEquals(404, response.getStatusCode());
        assertEquals("User Not Found", response.getMessage());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void testSaveBooking_RoomNotAvailable() {
        // Given
        Booking existingBooking = new Booking();
        existingBooking.setCheckInDate(LocalDate.now().plusDays(1));
        existingBooking.setCheckOutDate(LocalDate.now().plusDays(3));
        testRoom.setBookings(Arrays.asList(existingBooking));

        Booking bookingRequest = new Booking();
        bookingRequest.setCheckInDate(LocalDate.now().plusDays(2)); // Overlaps with existing
        bookingRequest.setCheckOutDate(LocalDate.now().plusDays(4));

        when(roomRepository.findById(1L)).thenReturn(Optional.of(testRoom));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When
        Response response = bookingService.saveBooking(1L, 1L, bookingRequest);

        // Then
        assertEquals(404, response.getStatusCode());
        assertEquals("Room not Available for selected date range", response.getMessage());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void testFindBookingByConfirmationCode_Success() {
        // Given
        String confirmationCode = "ABC123";
        testBooking.setBookingConfirmationCode(confirmationCode);
        when(bookingRepository.findByBookingConfirmationCode(confirmationCode))
                .thenReturn(Optional.of(testBooking));

        // When
        Response response = bookingService.findBookingByConfirmationCode(confirmationCode);

        // Then
        assertEquals(200, response.getStatusCode());
        assertNotNull(response.getBooking());
        assertEquals("successful", response.getMessage());
        verify(bookingRepository, times(1)).findByBookingConfirmationCode(confirmationCode);
    }

    @Test
    void testFindBookingByConfirmationCode_NotFound() {
        // Given
        String confirmationCode = "INVALID";
        when(bookingRepository.findByBookingConfirmationCode(confirmationCode))
                .thenReturn(Optional.empty());

        // When
        Response response = bookingService.findBookingByConfirmationCode(confirmationCode);

        // Then
        assertEquals(404, response.getStatusCode());
        assertEquals("Booking Not Found", response.getMessage());
    }

    @Test
    void testGetAllBookings_Success() {
        // Given
        List<Booking> bookings = Arrays.asList(testBooking);
        when(bookingRepository.findAll(any(Sort.class))).thenReturn(bookings);

        // When
        Response response = bookingService.getAllBookings();

        // Then
        assertEquals(200, response.getStatusCode());
        assertNotNull(response.getBookingList());
        assertEquals(1, response.getBookingList().size());
        assertEquals("successful", response.getMessage());
        verify(bookingRepository, times(1)).findAll(any(Sort.class));
    }

    @Test
    void testCancelBooking_Success() {
        // Given
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));
        doNothing().when(bookingRepository).deleteById(1L);

        // When
        Response response = bookingService.cancelBooking(1L);

        // Then
        assertEquals(200, response.getStatusCode());
        assertEquals("successful", response.getMessage());
        verify(bookingRepository, times(1)).findById(1L);
        verify(bookingRepository, times(1)).deleteById(1L);
    }

    @Test
    void testCancelBooking_NotFound() {
        // Given
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        Response response = bookingService.cancelBooking(1L);

        // Then
        assertEquals(404, response.getStatusCode());
        assertEquals("Booking Does Not Exist", response.getMessage());
        verify(bookingRepository, never()).deleteById(anyLong());
    }

    @Test
    void testSaveBooking_DatabaseError() {
        // Given
        Booking bookingRequest = new Booking();
        bookingRequest.setCheckInDate(LocalDate.now().plusDays(1));
        bookingRequest.setCheckOutDate(LocalDate.now().plusDays(3));

        when(roomRepository.findById(1L)).thenReturn(Optional.of(testRoom));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(bookingRepository.save(any(Booking.class)))
                .thenThrow(new DataAccessException("DB Error") {});

        // When
        Response response = bookingService.saveBooking(1L, 1L, bookingRequest);

        // Then
        assertEquals(500, response.getStatusCode());
        assertTrue(response.getMessage().contains("Database error"));
    }

    @Test
    void testSaveBooking_NullRoomId() {
        // Given
        Booking bookingRequest = new Booking();
        bookingRequest.setCheckInDate(LocalDate.now().plusDays(1));
        bookingRequest.setCheckOutDate(LocalDate.now().plusDays(3));

        // When
        Response response = bookingService.saveBooking(null, 1L, bookingRequest);

        // Then
        assertEquals(404, response.getStatusCode());
        assertEquals("Missing identifier for room", response.getMessage());
    }

    @Test
    void testSaveBooking_NullUserId() {
        // Given
        Booking bookingRequest = new Booking();
        bookingRequest.setCheckInDate(LocalDate.now().plusDays(1));
        bookingRequest.setCheckOutDate(LocalDate.now().plusDays(3));

        // When
        Response response = bookingService.saveBooking(1L, null, bookingRequest);

        // Then
        assertEquals(404, response.getStatusCode());
        assertEquals("Missing identifier for user", response.getMessage());
    }
}

