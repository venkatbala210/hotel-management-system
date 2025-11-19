package com.hotelbooking.hotelmanagement.service.interfac;

import com.hotelbooking.hotelmanagement.dto.Response;
import com.hotelbooking.hotelmanagement.entity.Booking;

public interface IBookingService {

    Response saveBooking(Long roomId, Long userId, Booking bookingRequest);

    Response findBookingByConfirmationCode(String confirmationCode);

    Response getAllBookings();

    Response cancelBooking(Long bookingId);

}

