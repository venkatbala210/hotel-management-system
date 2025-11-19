package com.hotelbooking.hotelmanagement.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hotelbooking.hotelmanagement.entity.Booking;

import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Optional<Booking> findByBookingConfirmationCode(String confirmationCode);
}

