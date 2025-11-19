package com.hotelbooking.hotelmanagement.repo;

import com.hotelbooking.hotelmanagement.entity.Booking;
import com.hotelbooking.hotelmanagement.entity.Room;
import com.hotelbooking.hotelmanagement.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BookingRepository bookingRepository;

    @Test
    void testFindByBookingConfirmationCode_Success() {
        // Given
        User user = new User();
        user.setEmail("test@example.com");
        user.setName("Test User");
        user.setPassword("password123");
        user.setPhoneNumber("1234567890");
        user.setRole("USER");
        User savedUser = entityManager.persistAndFlush(user);

        Room room = new Room();
        room.setRoomType("Deluxe");
        room.setRoomPrice(new java.math.BigDecimal("150.00"));
        Room savedRoom = entityManager.persistAndFlush(room);

        Booking booking = new Booking();
        booking.setCheckInDate(LocalDate.now().plusDays(1));
        booking.setCheckOutDate(LocalDate.now().plusDays(3));
        booking.setNumOfAdults(2);
        booking.setNumOfChildren(0);
        booking.setBookingConfirmationCode("ABC123");
        booking.setUser(savedUser);
        booking.setRoom(savedRoom);
        entityManager.persistAndFlush(booking);

        // When
        Optional<Booking> found = bookingRepository.findByBookingConfirmationCode("ABC123");

        // Then
        assertTrue(found.isPresent());
        assertEquals("ABC123", found.get().getBookingConfirmationCode());
        assertEquals(savedUser.getId(), found.get().getUser().getId());
        assertEquals(savedRoom.getId(), found.get().getRoom().getId());
    }

    @Test
    void testFindByBookingConfirmationCode_NotFound() {
        // When
        Optional<Booking> found = bookingRepository.findByBookingConfirmationCode("INVALID");

        // Then
        assertFalse(found.isPresent());
    }

    @Test
    void testSaveBooking() {
        // Given
        User user = new User();
        user.setEmail("test@example.com");
        user.setName("Test User");
        user.setPassword("password123");
        user.setPhoneNumber("1234567890");
        user.setRole("USER");
        User savedUser = entityManager.persistAndFlush(user);

        Room room = new Room();
        room.setRoomType("Deluxe");
        room.setRoomPrice(new java.math.BigDecimal("150.00"));
        Room savedRoom = entityManager.persistAndFlush(room);

        Booking booking = new Booking();
        booking.setCheckInDate(LocalDate.now().plusDays(1));
        booking.setCheckOutDate(LocalDate.now().plusDays(3));
        booking.setNumOfAdults(2);
        booking.setNumOfChildren(0);
        booking.setBookingConfirmationCode("XYZ789");
        booking.setUser(savedUser);
        booking.setRoom(savedRoom);

        // When
        Booking saved = bookingRepository.save(booking);

        // Then
        assertNotNull(saved.getId());
        assertEquals("XYZ789", saved.getBookingConfirmationCode());
        assertNotNull(saved.getUser());
        assertNotNull(saved.getRoom());
    }

    @Test
    void testDeleteBooking() {
        // Given
        User user = new User();
        user.setEmail("test@example.com");
        user.setName("Test User");
        user.setPassword("password123");
        user.setPhoneNumber("1234567890");
        user.setRole("USER");
        User savedUser = entityManager.persistAndFlush(user);

        Room room = new Room();
        room.setRoomType("Deluxe");
        room.setRoomPrice(new java.math.BigDecimal("150.00"));
        Room savedRoom = entityManager.persistAndFlush(room);

        Booking booking = new Booking();
        booking.setCheckInDate(LocalDate.now().plusDays(1));
        booking.setCheckOutDate(LocalDate.now().plusDays(3));
        booking.setNumOfAdults(2);
        booking.setNumOfChildren(0);
        booking.setBookingConfirmationCode("DELETE123");
        booking.setUser(savedUser);
        booking.setRoom(savedRoom);
        Booking saved = entityManager.persistAndFlush(booking);
        Long bookingId = saved.getId();
        assertNotNull(bookingId);

        // When
        bookingRepository.deleteById(bookingId);
        entityManager.flush();

        // Then
        Optional<Booking> found = bookingRepository.findById(bookingId);
        assertFalse(found.isPresent());
    }
}

