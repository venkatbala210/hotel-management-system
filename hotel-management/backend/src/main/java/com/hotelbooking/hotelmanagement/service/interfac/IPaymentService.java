package com.hotelbooking.hotelmanagement.service.interfac;

import com.hotelbooking.hotelmanagement.dto.Response;

import java.math.BigDecimal;

public interface IPaymentService {
    Response processPayment(Long bookingId, BigDecimal amount);
    Response getPaymentByBookingId(Long bookingId);
}

