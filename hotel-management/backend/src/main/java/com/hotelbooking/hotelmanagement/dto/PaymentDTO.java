package com.hotelbooking.hotelmanagement.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentDTO {

    private Long id;
    private Long bookingId;
    private BigDecimal amount;
    private String status; // SUCCESS or FAILED
    private LocalDateTime paymentDate;
}

