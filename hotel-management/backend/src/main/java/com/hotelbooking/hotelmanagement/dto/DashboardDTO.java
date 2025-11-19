package com.hotelbooking.hotelmanagement.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DashboardDTO {
    // User Statistics
    private Long totalUsers;
    private Long totalAdmins;
    private Long totalRegularUsers;
    private List<UserDTO> allUsers;

    // Booking Statistics
    private Long totalBookings;
    private Long confirmedBookings;
    private Long cancelledBookings;
    private List<BookingDTO> allBookings;

    // Room Statistics
    private Long totalRooms;
    private Long availableRooms;
    private Long bookedRooms;
    private List<RoomDTO> allRooms;

    // Payment Statistics
    private BigDecimal totalRevenue;
    private BigDecimal successfulPayments;
    private BigDecimal failedPayments;
    private Long totalPayments;
    private Long successfulPaymentCount;
    private Long failedPaymentCount;
    private List<PaymentDTO> allPayments;
}

