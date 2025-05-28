// src/main/java/com/example/bookify/dto/ReviewDTO.java
package com.example.bookify.dto;


/**
 * Data transfer object (DTO) for Review information.
 * Represents data transferred between client and server for Review operations.
 */
public class ReviewDTO {
    private Long bookingId;
    private int rating;
    private String comment;

    public ReviewDTO() {}

    /**
     * Constructs a ReviewDTO with specified attributes.
     *
     * @param bookingId The ID of the booking associated with the review.
     * @param rating The rating given in the review.
     * @param comment The comment associated with the review.
     */
    public ReviewDTO(Long bookingId, int rating, String comment) {
        this.bookingId = bookingId;
        this.rating    = rating;
        this.comment   = comment;
    }

    public Long getBookingId() { return bookingId; }
    public int   getRating()    { return rating; }
    public String getComment()  { return comment; }

    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }
    public void setRating(int rating)         { this.rating = rating; }
    public void setComment(String comment)   { this.comment = comment; }
}
