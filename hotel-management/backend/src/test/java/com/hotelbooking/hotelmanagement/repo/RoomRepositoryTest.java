package com.hotelbooking.hotelmanagement.repo;

import com.hotelbooking.hotelmanagement.entity.Room;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class RoomRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RoomRepository roomRepository;

    @Test
    void testFindDistinctRoomTypes() {
        // Given
        Room room1 = new Room();
        room1.setRoomType("Deluxe");
        room1.setRoomPrice(new BigDecimal("150.00"));
        entityManager.persistAndFlush(room1);

        Room room2 = new Room();
        room2.setRoomType("Standard");
        room2.setRoomPrice(new BigDecimal("100.00"));
        entityManager.persistAndFlush(room2);

        Room room3 = new Room();
        room3.setRoomType("Deluxe");
        room3.setRoomPrice(new BigDecimal("150.00"));
        entityManager.persistAndFlush(room3);

        // When
        List<String> roomTypes = roomRepository.findDistinctRoomTypes();

        // Then
        assertNotNull(roomTypes);
        assertTrue(roomTypes.contains("Deluxe"));
        assertTrue(roomTypes.contains("Standard"));
        assertEquals(2, roomTypes.size()); // Should be distinct
    }

    @Test
    void testSaveRoom() {
        // Given
        Room room = new Room();
        room.setRoomType("Suite");
        room.setRoomPrice(new BigDecimal("200.00"));
        room.setRoomDescription("A luxurious suite");
        room.setRoomPhotoUrl("https://example.com/suite.jpg");

        // When
        Room saved = roomRepository.save(room);

        // Then
        assertNotNull(saved.getId());
        assertEquals("Suite", saved.getRoomType());
        assertEquals(new BigDecimal("200.00"), saved.getRoomPrice());
    }

    @Test
    void testFindById() {
        // Given
        Room room = new Room();
        room.setRoomType("Deluxe");
        room.setRoomPrice(new BigDecimal("150.00"));
        Room saved = entityManager.persistAndFlush(room);
        Long roomId = saved.getId();
        assertNotNull(roomId);

        // When
        Optional<Room> found = roomRepository.findById(roomId);

        // Then
        assertTrue(found.isPresent());
        assertEquals("Deluxe", found.get().getRoomType());
    }

    @Test
    void testDeleteRoom() {
        // Given
        Room room = new Room();
        room.setRoomType("Deluxe");
        room.setRoomPrice(new BigDecimal("150.00"));
        Room saved = entityManager.persistAndFlush(room);
        Long roomId = saved.getId();
        assertNotNull(roomId);

        // When
        roomRepository.deleteById(roomId);
        entityManager.flush();

        // Then
        Optional<Room> found = roomRepository.findById(roomId);
        assertFalse(found.isPresent());
    }

    @Test
    void testGetAllAvailableRooms() {
        // Given - Create rooms without bookings
        Room room1 = new Room();
        room1.setRoomType("Deluxe");
        room1.setRoomPrice(new BigDecimal("150.00"));
        entityManager.persistAndFlush(room1);

        Room room2 = new Room();
        room2.setRoomType("Standard");
        room2.setRoomPrice(new BigDecimal("100.00"));
        entityManager.persistAndFlush(room2);

        // When
        List<Room> availableRooms = roomRepository.getAllAvailableRooms();

        // Then
        assertNotNull(availableRooms);
        assertTrue(availableRooms.size() >= 2);
    }
}

