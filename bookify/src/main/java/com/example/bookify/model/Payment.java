// src/main/java/com/example/bookify/model/Payment.java
package com.example.bookify.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity class representing a payment made for a booking.
 */
@Entity
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = true)
    private LocalDateTime paidAt;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    private String transactionId;

    // --- Builder ---
    private Payment(Builder b) {
        this.booking       = b.booking;
        this.amount        = b.amount;
        this.paidAt        = b.paidAt;
        this.status        = b.status;
        this.transactionId = b.transactionId;
    }

    public Payment() {

    }

    /**
     * Builder pattern for constructing Payment instances.
     */
    public static class Builder {
        private Booking booking;
        private BigDecimal amount;
        private LocalDateTime paidAt;
        private PaymentStatus status;
        private String transactionId;

        public Builder booking(Booking b)        { this.booking = b;       return this; }
        public Builder amount(BigDecimal a)      { this.amount = a;        return this; }
        public Builder paidAt(LocalDateTime d)   { this.paidAt = d;        return this; }
        public Builder status(PaymentStatus s)   { this.status = s;        return this; }
        public Builder transactionId(String t)   { this.transactionId = t; return this; }

        /**
         * Builds the Payment instance.
         *
         * @return constructed Payment object.
         */
        public Payment build() {
            return new Payment(this);
        }
    }

    /**
     * Provides a new instance of the Builder for Payment class.
     *
     * @return new instance of Builder.
     */
    public static Builder builder() {
        return new Builder();
    }

    // --- Getters & Setters ---
    public Long getId()                         { return id; }
    public void setId(Long id)                  { this.id = id; }

    public Booking getBooking()                 { return booking; }
    public void setBooking(Booking booking)     { this.booking = booking; }

    public BigDecimal getAmount()               { return amount; }
    public void setAmount(BigDecimal amount)    { this.amount = amount; }

    public LocalDateTime getPaidAt()            { return paidAt; }
    public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }

    public PaymentStatus getStatus()            { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }

    public String getTransactionId()            { return transactionId; }
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
}
