package com.hotelbooking.hotelmanagement.repo;

import com.hotelbooking.hotelmanagement.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testFindByEmail_Success() {
        // Given
        User user = new User();
        user.setEmail("test@example.com");
        user.setName("Test User");
        user.setPassword("password123");
        user.setPhoneNumber("1234567890");
        user.setRole("USER");
        entityManager.persistAndFlush(user);

        // When
        Optional<User> found = userRepository.findByEmail("test@example.com");

        // Then
        assertTrue(found.isPresent());
        assertEquals("test@example.com", found.get().getEmail());
        assertEquals("Test User", found.get().getName());
    }

    @Test
    void testFindByEmail_NotFound() {
        // When
        Optional<User> found = userRepository.findByEmail("nonexistent@example.com");

        // Then
        assertFalse(found.isPresent());
    }

    @Test
    void testExistsByEmail_True() {
        // Given
        User user = new User();
        user.setEmail("test@example.com");
        user.setName("Test User");
        user.setPassword("password123");
        user.setPhoneNumber("1234567890");
        user.setRole("USER");
        entityManager.persistAndFlush(user);

        // When
        boolean exists = userRepository.existsByEmail("test@example.com");

        // Then
        assertTrue(exists);
    }

    @Test
    void testExistsByEmail_False() {
        // When
        boolean exists = userRepository.existsByEmail("nonexistent@example.com");

        // Then
        assertFalse(exists);
    }

    @Test
    void testSaveUser() {
        // Given
        User user = new User();
        user.setEmail("new@example.com");
        user.setName("New User");
        user.setPassword("password123");
        user.setPhoneNumber("1234567890");
        user.setRole("USER");

        // When
        User saved = userRepository.save(user);

        // Then
        assertNotNull(saved.getId());
        assertEquals("new@example.com", saved.getEmail());
    }

    @Test
    void testDeleteUser() {
        // Given
        User user = new User();
        user.setEmail("delete@example.com");
        user.setName("Delete User");
        user.setPassword("password123");
        user.setPhoneNumber("1234567890");
        user.setRole("USER");
        User saved = entityManager.persistAndFlush(user);
        Long userId = saved.getId();
        assertNotNull(userId);

        // When
        userRepository.deleteById(userId);
        entityManager.flush();

        // Then
        Optional<User> found = userRepository.findById(userId);
        assertFalse(found.isPresent());
    }
}

