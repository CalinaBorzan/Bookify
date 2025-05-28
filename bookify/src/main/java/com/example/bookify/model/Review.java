// src/main/java/com/example/bookify/model/Review.java
package com.example.bookify.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity class representing a review for a booking in the system.
 */
@Entity
@Table(name = "reviews")
@JsonIgnoreProperties({
        "booking", "moderationRemarks"
})
public class Review {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference            // <-- add this annotation
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @Column(nullable = false)
    private int rating;

    @Column(length = 2000)
    private String comment;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private boolean moderated;
    private String moderationRemarks;

    public Review() {}

    // getters
    @JsonProperty("author")
    public String getAuthor() {
        return booking != null
                ? booking.getUser().getUsername()
                : null;
    }
    public Long getId()                  { return id; }
    public Booking getBooking()          { return booking; }
    public int getRating()               { return rating; }
    public String getComment()           { return comment; }
    public LocalDateTime getCreatedAt()  { return createdAt; }
    public boolean isModerated()         { return moderated; }
    public String getModerationRemarks() { return moderationRemarks; }

    // setters
    public void setId(Long id)                          { this.id = id; }
    public void setBooking(Booking booking)              { this.booking = booking; }
    public void setRating(int rating)                    { this.rating = rating; }
    public void setComment(String comment)               { this.comment = comment; }
    public void setCreatedAt(LocalDateTime createdAt)    { this.createdAt = createdAt; }
    public void setModerated(boolean moderated)          { this.moderated = moderated; }
    public void setModerationRemarks(String remarks)     { this.moderationRemarks = remarks; }

    @JsonProperty("listingTitle")
    public String getListingTitle() {
        // assumes Booking has a getListing() → Listing with getTitle()
        return booking != null
                ? booking.getListing().getTitle()
                : null;
    }
}
