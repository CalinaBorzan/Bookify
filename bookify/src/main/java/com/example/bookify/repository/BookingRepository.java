package com.example.bookify.repository;

import com.example.bookify.model.Booking;
import com.example.bookify.model.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;


/**
 * Repository interface for accessing Booking entities in the database.
 */
public interface BookingRepository extends JpaRepository<Booking,Long> {


    /**
     * Retrieves all bookings associated with a specific user.
     *
     * @param userId The ID of the user whose bookings are to be retrieved.
     * @return A list of Booking entities associated with the user.
     */
    List<Booking> findByUserId(Long userId);
    /**
     * Counts the number of overlapping hotel bookings for a given hotel ID and date range.
     *
     * @param hotelId  The ID of the hotel listing.
     * @param checkIn  The check-in date.
     * @param checkOut The check-out date.
     * @return The number of overlapping hotel bookings.
     */
    @Query("""
      SELECT COUNT(b)
        FROM Booking b
       WHERE b.listing.id = :hotelId
         AND b.type = com.example.bookify.model.BookingType.HOTEL
         AND b.status = com.example.bookify.model.BookingStatus.CONFIRMED
         AND b.checkIn < :checkOut
         AND b.checkOut > :checkIn
    """)
    long countHotelOverlaps(
            @Param("hotelId") Long hotelId,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut
    );


    /**
     * Retrieves all bookings associated with listings owned by a specific owner.
     *
     * @param ownerId The ID of the owner whose listings' bookings are to be retrieved.
     * @return A list of Booking entities associated with listings owned by the owner.
     */
    @Query("""
    select b
      from Booking b
      join b.listing l
     where l.createdBy.id = :ownerId
    """)
    List<Booking> findByListingOwnerId(@Param("ownerId") Long ownerId);


    /**
     * Calculates the total number of guests for a specific flight booking.
     *
     * @param flightId The ID of the flight listing.
     * @return The total number of guests booked for the flight.
     */
    @Query("""
      SELECT COALESCE(SUM(b.numGuests),0)
        FROM Booking b
       WHERE b.listing.id = :flightId
         AND b.type = com.example.bookify.model.BookingType.FLIGHT
         AND b.status = com.example.bookify.model.BookingStatus.CONFIRMED
    """)
    int sumFlightGuests(@Param("flightId") Long flightId);


    /**
     * Calculates the total number of guests for a specific event booking.
     *
     * @param eventId The ID of the event listing.
     * @return The total number of guests booked for the event.
     */
    @Query("""
      SELECT COALESCE(SUM(b.numGuests),0)
        FROM Booking b
       WHERE b.listing.id = :eventId
         AND b.type = com.example.bookify.model.BookingType.EVENT
         AND b.status = com.example.bookify.model.BookingStatus.CONFIRMED
    """)
    int sumEventGuests(@Param("eventId") Long eventId);
}
