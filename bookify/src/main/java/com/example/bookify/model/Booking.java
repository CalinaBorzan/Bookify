package com.example.bookify.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


/**
 * Entity class representing a booking in the system.
 */
@Entity
@Table(name="bookings")
public class Booking {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional=false) @JoinColumn(name="user_id")
    private User user;
    @ManyToOne(optional=false) @JoinColumn(name="listing_id")
    private Listing listing;
    @Enumerated(EnumType.STRING) @Column(nullable=false)
    private BookingType type;
    @Enumerated(EnumType.STRING) @Column(nullable=false)
    private BookingStatus status;
    @Column(nullable=false)
    private LocalDateTime bookedAt;

    @Column(nullable=false)
    private LocalDate checkIn;

    @Column(nullable=false)
    private LocalDate checkOut;

    @Column(nullable=false)
    private Integer numGuests;


    @OneToMany(mappedBy="booking", cascade=CascadeType.ALL, orphanRemoval=true)
    private List<Payment> payments = new ArrayList<>();

    private Booking(Builder b) {
        this.user     = b.user;
        this.listing  = b.listing;
        this.type     = b.type;
        this.status   = b.status;
        this.bookedAt = b.bookedAt;
        this.checkIn=b.checkIn;
        this.checkOut=b.checkOut;
        this.numGuests = b.numGuests;
    }

    public Booking() {

    }

    /**
     * Builder pattern for constructing Booking instances.
     */
    public static class Builder {
        private User user;
        private Listing listing;
        private BookingType type;
        private BookingStatus status;
        private LocalDateTime bookedAt;
        private LocalDate checkIn;
        private LocalDate checkOut;
        private Integer numGuests;

        public Builder user(User u)          { this.user = u; return this; }
        public Builder listing(Listing l)    { this.listing = l; return this; }
        public Builder type(BookingType t)   { this.type = t; return this; }
        public Builder status(BookingStatus s){ this.status = s; return this; }
        public Builder bookedAt(LocalDateTime dt){ this.bookedAt = dt; return this; }
        public Builder checkIn(LocalDate d){ this.checkIn = d; return this; }
        public Builder checkOut(LocalDate d){ this.checkOut = d; return this; }
        public Builder numGuests(Integer n){ this.numGuests = n; return this; }

        /**
         * Builds the Booking instance with the provided parameters.
         * @return Constructed Booking instance.
         */
        public Booking build() { return new Booking(this); }
    }

    public static Builder builder() { return new Builder(); }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public Listing getListing() { return listing; }
    public BookingType getType() { return type; }
    public BookingStatus getStatus() { return status; }
    public LocalDateTime getBookedAt() { return bookedAt; }
    public void setId(Long id) { this.id = id; }
    public void setUser(User u) { this.user = u; }
    public void setListing(Listing l) { this.listing = l; }
    public void setType(BookingType t) { this.type = t; }
    public void setStatus(BookingStatus s) { this.status = s; }
    public void setBookedAt(LocalDateTime dt) { this.bookedAt = dt; }
    public List<Payment> getPayments() { return payments; }
    public void setPayments(List<Payment> payments) { this.payments = payments; }

    public LocalDate getCheckIn() { return checkIn; }
    public void setCheckIn(LocalDate checkIn) { this.checkIn = checkIn; }

    public LocalDate getCheckOut() { return checkOut; }
    public void setCheckOut(LocalDate checkOut) { this.checkOut = checkOut; }

    public Integer getNumGuests() { return numGuests; }
    public void setNumGuests(Integer numGuests) { this.numGuests = numGuests; }
}


