package com.hotelbooking.hotelmanagement.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.hotelbooking.hotelmanagement.dto.DashboardDTO;
import com.hotelbooking.hotelmanagement.dto.Response;
import com.hotelbooking.hotelmanagement.entity.Booking;
import com.hotelbooking.hotelmanagement.entity.Payment;
import com.hotelbooking.hotelmanagement.entity.Room;
import com.hotelbooking.hotelmanagement.entity.User;
import com.hotelbooking.hotelmanagement.repo.BookingRepository;
import com.hotelbooking.hotelmanagement.repo.PaymentRepository;
import com.hotelbooking.hotelmanagement.repo.RoomRepository;
import com.hotelbooking.hotelmanagement.repo.UserRepository;
import com.hotelbooking.hotelmanagement.service.interfac.IAdminDashboardService;
import com.hotelbooking.hotelmanagement.utils.Utils;

@Service
public class AdminDashboardService implements IAdminDashboardService {

    private static final Logger log = LoggerFactory.getLogger(AdminDashboardService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Override
    public Response getDashboardStatistics() {
        Response response = new Response();

        try {
            DashboardDTO dashboard = new DashboardDTO();

            // User Statistics
            List<User> allUsers = userRepository.findAll();
            dashboard.setTotalUsers((long) allUsers.size());
            dashboard.setTotalAdmins(allUsers.stream()
                    .filter(user -> "ADMIN".equals(user.getRole()))
                    .count());
            dashboard.setTotalRegularUsers(allUsers.stream()
                    .filter(user -> "USER".equals(user.getRole()))
                    .count());
            dashboard.setAllUsers(allUsers.stream()
                    .map(Utils::mapUserEntityToUserDTO)
                    .collect(Collectors.toList()));

            // Booking Statistics
            List<Booking> allBookings = bookingRepository.findAll();
            dashboard.setTotalBookings((long) allBookings.size());
            dashboard.setConfirmedBookings(allBookings.stream()
                    .filter(booking -> booking.getStatus() == Booking.BookingStatus.CONFIRMED)
                    .count());
            dashboard.setCancelledBookings(allBookings.stream()
                    .filter(booking -> booking.getStatus() == Booking.BookingStatus.CANCELLED)
                    .count());
            dashboard.setAllBookings(allBookings.stream()
                    .map(booking -> Utils.mapBookingEntityToBookingDTOPlusBookedRooms(booking, true))
                    .collect(Collectors.toList()));

            // Room Statistics
            List<Room> allRooms = roomRepository.findAll();
            dashboard.setTotalRooms((long) allRooms.size());
            
            // Get booked room IDs
            List<Long> bookedRoomIds = allBookings.stream()
                    .filter(booking -> booking.getStatus() == Booking.BookingStatus.CONFIRMED)
                    .filter(booking -> booking.getRoom() != null)
                    .map(booking -> booking.getRoom().getId())
                    .filter(id -> id != null)
                    .distinct()
                    .collect(Collectors.toList());
            
            dashboard.setBookedRooms((long) bookedRoomIds.size());
            dashboard.setAvailableRooms(dashboard.getTotalRooms() - dashboard.getBookedRooms());
            dashboard.setAllRooms(allRooms.stream()
                    .map(Utils::mapRoomEntityToRoomDTO)
                    .collect(Collectors.toList()));

            // Payment Statistics
            List<Payment> allPayments = paymentRepository.findAll();
            dashboard.setTotalPayments((long) allPayments.size());
            
            BigDecimal totalRevenue = BigDecimal.ZERO;
            BigDecimal successfulPayments = BigDecimal.ZERO;
            BigDecimal failedPayments = BigDecimal.ZERO;
            long successfulCount = 0;
            long failedCount = 0;

            for (Payment payment : allPayments) {
                totalRevenue = totalRevenue.add(payment.getAmount());
                if (payment.getStatus() == Payment.PaymentStatus.SUCCESS) {
                    successfulPayments = successfulPayments.add(payment.getAmount());
                    successfulCount++;
                } else {
                    failedPayments = failedPayments.add(payment.getAmount());
                    failedCount++;
                }
            }

            dashboard.setTotalRevenue(totalRevenue);
            dashboard.setSuccessfulPayments(successfulPayments);
            dashboard.setFailedPayments(failedPayments);
            dashboard.setSuccessfulPaymentCount(successfulCount);
            dashboard.setFailedPaymentCount(failedCount);
            dashboard.setAllPayments(allPayments.stream()
                    .map(Utils::mapPaymentEntityToPaymentDTO)
                    .collect(Collectors.toList()));

            response.setStatusCode(200);
            response.setMessage("Dashboard statistics retrieved successfully");
            response.setDashboard(dashboard);

        } catch (DataAccessException e) {
            response.setStatusCode(500);
            response.setMessage("Database error while retrieving dashboard statistics: " + e.getMessage());
            log.error("Database error retrieving dashboard statistics", e);
        } catch (RuntimeException e) {
            response.setStatusCode(500);
            response.setMessage("Error retrieving dashboard statistics: " + e.getMessage());
            log.error("Unexpected error retrieving dashboard statistics", e);
        }

        return response;
    }
}

