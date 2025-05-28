// src/main/java/com/example/bookify/dto/PaymentDTO.java
package com.example.bookify.dto;

import java.math.BigDecimal;

/**
 * Data transfer object (DTO) for Payment information.
 * Represents data transferred between client and server for Payment operations.
 */
public class PaymentDTO {
    private Long bookingId;
    private BigDecimal amount;
    private String transactionId;

    public PaymentDTO() {}


    /**
     * Constructs a PaymentDTO with specified attributes.
     *
     * @param bookingId The ID of the booking associated with the payment.
     * @param amount The amount of the payment.
     * @param transactionId The transaction ID associated with the payment.
     */
    public PaymentDTO(Long bookingId, BigDecimal amount, String transactionId) {
        this.bookingId    = bookingId;
        this.amount       = amount;
        this.transactionId= transactionId;
    }

    public Long getBookingId()           { return bookingId; }
    public BigDecimal getAmount()        { return amount; }
    public String getTransactionId()     { return transactionId; }

    public void setBookingId(Long bookingId)       { this.bookingId = bookingId; }
    public void setAmount(BigDecimal amount)       { this.amount = amount; }
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
}
