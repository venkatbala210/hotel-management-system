package com.hotelbooking.hotelmanagement.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.hotelbooking.hotelmanagement.dto.PaymentDTO;
import com.hotelbooking.hotelmanagement.dto.Response;
import com.hotelbooking.hotelmanagement.entity.Booking;
import com.hotelbooking.hotelmanagement.entity.Payment;
import com.hotelbooking.hotelmanagement.exception.OurException;
import com.hotelbooking.hotelmanagement.repo.BookingRepository;
import com.hotelbooking.hotelmanagement.repo.PaymentRepository;
import com.hotelbooking.hotelmanagement.service.interfac.IPaymentService;
import com.hotelbooking.hotelmanagement.utils.Utils;

@Service
public class PaymentService implements IPaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private BookingRepository bookingRepository;

    @Override
    public Response processPayment(Long bookingId, BigDecimal amount) {
        Response response = new Response();
        
        try {
            long resolvedBookingId = requireId(bookingId, "booking");
            
            // Find the booking
            Booking booking = bookingRepository.findById(resolvedBookingId)
                    .orElseThrow(() -> new OurException("Booking Not Found"));
            
            // Check if payment already exists
            if (paymentRepository.findByBookingId(resolvedBookingId).isPresent()) {
                throw new OurException("Payment already processed for this booking");
            }
            
            // Simulate payment processing (90% success rate for demo)
            Payment.PaymentStatus paymentStatus = simulatePaymentProcessing();
            
            // Create payment entity
            Payment payment = new Payment();
            payment.setBooking(booking);
            payment.setAmount(amount);
            payment.setStatus(paymentStatus);
            payment.setPaymentDate(LocalDateTime.now());
            
            // Save payment
            Payment savedPayment = paymentRepository.save(payment);
            
            // Map to DTO
            PaymentDTO paymentDTO = Utils.mapPaymentEntityToPaymentDTO(savedPayment);
            
            response.setStatusCode(200);
            response.setMessage("Payment processed successfully");
            response.setPayment(paymentDTO);
            
        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (DataAccessException e) {
            response.setStatusCode(500);
            response.setMessage("Database error while processing payment: " + e.getMessage());
            log.error("Database error processing payment for booking {}", bookingId, e);
        } catch (RuntimeException e) {
            response.setStatusCode(500);
            response.setMessage("Error processing payment: " + e.getMessage());
            log.error("Unexpected error processing payment for booking {}", bookingId, e);
        }
        
        return response;
    }

    @Override
    public Response getPaymentByBookingId(Long bookingId) {
        Response response = new Response();
        
        try {
            long resolvedBookingId = requireId(bookingId, "booking");
            
            Payment payment = paymentRepository.findByBookingId(resolvedBookingId)
                    .orElseThrow(() -> new OurException("Payment not found for this booking"));
            
            PaymentDTO paymentDTO = Utils.mapPaymentEntityToPaymentDTO(payment);
            
            response.setStatusCode(200);
            response.setMessage("successful");
            response.setPayment(paymentDTO);
            
        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (DataAccessException e) {
            response.setStatusCode(500);
            response.setMessage("Database error while retrieving payment: " + e.getMessage());
            log.error("Database error retrieving payment for booking {}", bookingId, e);
        } catch (RuntimeException e) {
            response.setStatusCode(500);
            response.setMessage("Error retrieving payment: " + e.getMessage());
            log.error("Unexpected error retrieving payment for booking {}", bookingId, e);
        }
        
        return response;
    }

    /**
     * Simulates payment processing with 90% success rate
     * In a real application, this would integrate with a payment gateway
     */
    private Payment.PaymentStatus simulatePaymentProcessing() {
        Random random = new Random();
        // 90% success rate
        return random.nextInt(100) < 90 ? Payment.PaymentStatus.SUCCESS : Payment.PaymentStatus.FAILED;
    }
    
    private long requireId(Long id, String resourceName) {
        if (id == null) {
            throw new OurException("Missing identifier for " + resourceName);
        }
        return id;
    }
}

