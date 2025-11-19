# Hotel Management System - Test Suite

This directory contains comprehensive unit and integration tests for the Hotel Management System backend.

## Test Structure

### Unit Tests

#### Service Tests
- **UserServiceTest.java** - Tests for user registration, login, profile management
- **RoomServiceTest.java** - Tests for room CRUD operations and availability checks
- **BookingServiceTest.java** - Tests for booking creation, cancellation, and validation

#### Controller Tests
- **AuthControllerTest.java** - Tests for authentication endpoints (register, login)
- **UserControllerTest.java** - Tests for user management endpoints
- **RoomControllerTest.java** - Tests for room management endpoints
- **BookingControllerTest.java** - Tests for booking endpoints

#### Repository Tests
- **UserRepositoryTest.java** - Tests for user data access operations
- **RoomRepositoryTest.java** - Tests for room data access operations
- **BookingRepositoryTest.java** - Tests for booking data access operations

### Integration Tests

#### End-to-End Tests
- **UserWorkflowIntegrationTest.java** - Complete user workflows including:
  - User registration and login
  - Room browsing and booking
  - Booking management (view, cancel)
  - Admin workflows
  - Room availability checks

## Running Tests

### Run All Tests
```bash
mvn test
```

### Run Specific Test Class
```bash
mvn test -Dtest=UserServiceTest
```

### Run Tests by Category
```bash
# Run only unit tests
mvn test -Dtest="*Test"

# Run only integration tests
mvn test -Dtest="*IntegrationTest"
```

### Run with Coverage
```bash
mvn test jacoco:report
```

## Test Coverage

The test suite covers:
- ✅ User registration and authentication
- ✅ User profile management
- ✅ Room CRUD operations
- ✅ Room availability checking
- ✅ Booking creation and validation
- ✅ Booking cancellation
- ✅ Admin operations
- ✅ Error handling and edge cases
- ✅ Security and authorization

## Test Dependencies

- **JUnit 5** - Testing framework
- **Mockito** - Mocking framework
- **Spring Boot Test** - Spring testing utilities
- **Spring Security Test** - Security testing utilities
- **H2 Database** - In-memory database for integration tests

## Test Configuration

Tests use an in-memory H2 database configured in `application-test.properties` to avoid requiring a MySQL instance for testing.

## Notes

- Integration tests use `@Transactional` to ensure test isolation
- MockMvc is used for controller testing without starting a full server
- Repository tests use `@DataJpaTest` for focused JPA testing
- Service tests use Mockito to mock dependencies

