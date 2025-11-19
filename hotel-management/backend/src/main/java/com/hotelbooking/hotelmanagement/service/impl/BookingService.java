package com.hotelbooking.hotelmanagement.service.impl;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.hotelbooking.hotelmanagement.dto.BookingDTO;
import com.hotelbooking.hotelmanagement.dto.Response;
import com.hotelbooking.hotelmanagement.entity.Booking;
import com.hotelbooking.hotelmanagement.entity.Room;
import com.hotelbooking.hotelmanagement.entity.User;
import com.hotelbooking.hotelmanagement.exception.OurException;
import com.hotelbooking.hotelmanagement.repo.BookingRepository;
import com.hotelbooking.hotelmanagement.repo.RoomRepository;
import com.hotelbooking.hotelmanagement.repo.UserRepository;
import com.hotelbooking.hotelmanagement.service.interfac.IBookingService;
import com.hotelbooking.hotelmanagement.utils.Utils;

@Service
public class BookingService implements IBookingService {

    private static final Logger log = LoggerFactory.getLogger(BookingService.class);
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private UserRepository userRepository;


    @Override
    public Response saveBooking(Long roomId, Long userId, Booking bookingRequest) {

        Response response = new Response();

        try {
            if (bookingRequest.getCheckOutDate().isBefore(bookingRequest.getCheckInDate())) {
                throw new IllegalArgumentException("Check in date must come after check out date");
            }
            long resolvedRoomId = requireId(roomId, "room");
            long resolvedUserId = requireId(userId, "user");
            Room room = roomRepository.findById(resolvedRoomId).orElseThrow(() -> new OurException("Room Not Found"));
            User user = userRepository.findById(resolvedUserId).orElseThrow(() -> new OurException("User Not Found"));

            List<Booking> existingBookings = room.getBookings();

            // Filter out cancelled bookings when checking availability
            List<Booking> activeBookings = existingBookings.stream()
                    .filter(b -> b.getStatus() == null || b.getStatus() != Booking.BookingStatus.CANCELLED)
                    .toList();
            
            if (!roomIsAvailable(bookingRequest, activeBookings)) {
                throw new OurException("Room not Available for selected date range");
            }

            bookingRequest.setRoom(room);
            bookingRequest.setUser(user);
            String bookingConfirmationCode = Utils.generateRandomConfirmationCode(10);
            bookingRequest.setBookingConfirmationCode(bookingConfirmationCode);
            Booking savedBooking = bookingRepository.save(bookingRequest);
            
            // Calculate total price for payment
            long days = ChronoUnit.DAYS.between(
                bookingRequest.getCheckInDate(), 
                bookingRequest.getCheckOutDate()
            ) + 1;
            BigDecimal totalPrice = room.getRoomPrice().multiply(BigDecimal.valueOf(days));
            log.debug("Calculated total price {} for booking {}", totalPrice, savedBooking.getId());
            
            response.setStatusCode(200);
            response.setMessage("successful");
            response.setBookingConfirmationCode(bookingConfirmationCode);
            // Return booking ID and total price for payment processing
            BookingDTO bookingDTO = Utils.mapBookingEntityToBookingDTO(savedBooking);
            bookingDTO.setId(savedBooking.getId());
            response.setBooking(bookingDTO);
            // Store total price in a way frontend can access it
            // We'll add it to the booking DTO or return it separately

        } catch (IllegalArgumentException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (DataAccessException e) {
            response.setStatusCode(500);
            response.setMessage("Database error while saving booking " + e.getMessage());
            log.error("Database error while saving booking for room {} and user {}", roomId, userId, e);
        } catch (RuntimeException e) {
            response.setStatusCode(500);
            response.setMessage("Error Saving a booking: " + e.getMessage());
            log.error("Unexpected error while saving booking for room {} and user {}", roomId, userId, e);
        }
        return response;
    }


    @Override
    public Response findBookingByConfirmationCode(String confirmationCode) {

        Response response = new Response();

        try {
            Booking booking = bookingRepository.findByBookingConfirmationCode(confirmationCode).orElseThrow(() -> new OurException("Booking Not Found"));
            BookingDTO bookingDTO = Utils.mapBookingEntityToBookingDTOPlusBookedRooms(booking, true);
            response.setStatusCode(200);
            response.setMessage("successful");
            response.setBooking(bookingDTO);

        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (DataAccessException e) {
            response.setStatusCode(500);
            response.setMessage("Database error while finding booking " + e.getMessage());
            log.error("Database error retrieving booking by confirmation code {}", confirmationCode, e);
        } catch (RuntimeException e) {
            response.setStatusCode(500);
            response.setMessage("Error Finding a booking: " + e.getMessage());
            log.error("Unexpected error retrieving booking by confirmation code {}", confirmationCode, e);
        }
        return response;
    }

    @Override
    public Response getAllBookings() {

        Response response = new Response();

        try {
            List<Booking> bookingList = bookingRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
            List<BookingDTO> bookingDTOList = Utils.mapBookingListEntityToBookingListDTO(bookingList);
            response.setStatusCode(200);
            response.setMessage("successful");
            response.setBookingList(bookingDTOList);

        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (DataAccessException e) {
            response.setStatusCode(500);
            response.setMessage("Database error while fetching bookings " + e.getMessage());
            log.error("Database error retrieving all bookings", e);
        } catch (RuntimeException e) {
            response.setStatusCode(500);
            response.setMessage("Error Getting all bookings: " + e.getMessage());
            log.error("Unexpected error retrieving all bookings", e);
        }
        return response;
    }

    @Override
    public Response cancelBooking(Long bookingId) {

        Response response = new Response();

        try {
            long resolvedBookingId = requireId(bookingId, "booking");
            Booking booking = bookingRepository.findById(resolvedBookingId)
                    .orElseThrow(() -> new OurException("Booking Does Not Exist"));
            
            // Update status to CANCELLED instead of deleting
            booking.setStatus(Booking.BookingStatus.CANCELLED);
            bookingRepository.save(booking);
            
            response.setStatusCode(200);
            response.setMessage("Booking cancelled successfully");

        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (DataAccessException e) {
            response.setStatusCode(500);
            response.setMessage("Database error while cancelling booking " + e.getMessage());
            log.error("Database error cancelling booking {}", bookingId, e);
        } catch (RuntimeException e) {
            response.setStatusCode(500);
            response.setMessage("Error Cancelling a booking: " + e.getMessage());
            log.error("Unexpected error cancelling booking {}", bookingId, e);
        }
        return response;
    }

    private long requireId(Long id, String resourceName) {
        if (id == null) {
            throw new OurException("Missing identifier for " + resourceName);
        }
        return id;
    }

    private boolean roomIsAvailable(Booking bookingRequest, List<Booking> existingBookings) {

        return existingBookings.stream()
                .noneMatch(existingBooking ->
                        bookingRequest.getCheckInDate().equals(existingBooking.getCheckInDate())
                                || bookingRequest.getCheckOutDate().isBefore(existingBooking.getCheckOutDate())
                                || (bookingRequest.getCheckInDate().isAfter(existingBooking.getCheckInDate())
                                && bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckOutDate()))
                                || (bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckInDate())

                                && bookingRequest.getCheckOutDate().equals(existingBooking.getCheckOutDate()))
                                || (bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckInDate())

                                && bookingRequest.getCheckOutDate().isAfter(existingBooking.getCheckOutDate()))

                                || (bookingRequest.getCheckInDate().equals(existingBooking.getCheckOutDate())
                                && bookingRequest.getCheckOutDate().equals(existingBooking.getCheckInDate()))

                                || (bookingRequest.getCheckInDate().equals(existingBooking.getCheckOutDate())
                                && bookingRequest.getCheckOutDate().equals(bookingRequest.getCheckInDate()))
                );
    }
}

