package com.hotelbooking.hotelmanagement.controller;

import com.hotelbooking.hotelmanagement.dto.Response;
import com.hotelbooking.hotelmanagement.service.interfac.IPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    private IPaymentService paymentService;

    @PostMapping("/process/{bookingId}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public ResponseEntity<Response> processPayment(
            @PathVariable Long bookingId,
            @RequestBody PaymentRequest paymentRequest) {
        Response response = paymentService.processPayment(bookingId, paymentRequest.getAmount());
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/booking/{bookingId}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public ResponseEntity<Response> getPaymentByBookingId(@PathVariable Long bookingId) {
        Response response = paymentService.getPaymentByBookingId(bookingId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    // Inner class for payment request
    public static class PaymentRequest {
        private BigDecimal amount;

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }
    }
}

