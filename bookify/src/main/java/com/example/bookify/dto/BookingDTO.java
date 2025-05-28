package com.example.bookify.dto;

import com.example.bookify.model.BookingStatus;
import com.example.bookify.model.BookingType;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) for booking information.
 * Represents the data transferred between client and server for booking operations.
 */
public class BookingDTO {

    private Long id;
    private Long userId;
    private Long listingId;

    // ★ NEW — expose what the browser needs
    private String listingTitle;
    private BigDecimal listingPrice;

    private BookingType   type;
    private BookingStatus status;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime bookedAt;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkIn;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkOut;

    private Integer numGuests;
    private boolean payNow;


    public BookingDTO() {}

    public BookingDTO(
            Long id,
            Long userId,
            Long listingId,
            String listingTitle,          // ★
            BigDecimal listingPrice,          // ★
            BookingType type,
            BookingStatus status,
            LocalDateTime bookedAt,
            LocalDate checkIn,
            LocalDate checkOut,
            Integer numGuests,
            boolean payNow) {

        this.id            = id;
        this.userId        = userId;
        this.listingId     = listingId;
        this.listingTitle  = listingTitle;   // ★
        this.listingPrice  = listingPrice;   // ★
        this.type          = type;
        this.status        = status;
        this.bookedAt      = bookedAt;
        this.checkIn       = checkIn;
        this.checkOut      = checkOut;
        this.numGuests     = numGuests;
        this.payNow        = payNow;
    }

    /* ---------- Fluent Builder ---------- */
    public static class Builder {
        private Long   id;
        private Long   userId;
        private Long   listingId;
        private String listingTitle;  // ★
        private BigDecimal listingPrice;  // ★
        private BookingType   type;
        private BookingStatus status;
        private LocalDateTime bookedAt;
        private LocalDate     checkIn;
        private LocalDate     checkOut;
        private Integer       numGuests;
        private boolean       payNow;

        public Builder id(Long v)            { id = v; return this; }
        public Builder userId(Long v)        { userId = v; return this; }
        public Builder listingId(Long v)     { listingId = v; return this; }
        public Builder listingTitle(String v){ listingTitle = v; return this; }   // ★
        public Builder listingPrice(BigDecimal v){ listingPrice = v; return this; }   // ★
        public Builder type(BookingType v)   { type = v; return this; }
        public Builder status(BookingStatus v){ status = v; return this; }
        public Builder bookedAt(LocalDateTime v){ bookedAt = v; return this; }
        public Builder checkIn(LocalDate v)  { checkIn = v; return this; }
        public Builder checkOut(LocalDate v) { checkOut = v; return this; }
        public Builder numGuests(Integer v)  { numGuests = v; return this; }
        public Builder payNow(boolean v)     { payNow = v; return this; }

        public BookingDTO build() {
            return new BookingDTO(
                    id,userId,listingId,
                    listingTitle,listingPrice,  // ★
                    type,status,bookedAt,
                    checkIn,checkOut,numGuests,payNow
            );
        }
    }
    public static Builder builder(){ return new Builder(); }

    /* ---------- Getters ---------- */

    public String getListingTitle()  { return listingTitle; }   // ★
    public BigDecimal getListingPrice()  { return listingPrice; }   // ★

    // --- Getters & Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getListingId() { return listingId; }
    public void setListingId(Long listingId) { this.listingId = listingId; }

    public BookingType getType() { return type; }
    public void setType(BookingType type) { this.type = type; }

    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }

    public LocalDateTime getBookedAt() { return bookedAt; }
    public void setBookedAt(LocalDateTime bookedAt) { this.bookedAt = bookedAt; }

    public LocalDate getCheckIn() { return checkIn; }
    public void setCheckIn(LocalDate checkIn) { this.checkIn = checkIn; }

    public LocalDate getCheckOut() { return checkOut; }
    public void setCheckOut(LocalDate checkOut) { this.checkOut = checkOut; }

    public Integer getNumGuests() { return numGuests; }
    public void setNumGuests(Integer numGuests) { this.numGuests = numGuests; }

    public boolean isPayNow() { return payNow; }
    public void setPayNow(boolean payNow) { this.payNow = payNow; }
}
