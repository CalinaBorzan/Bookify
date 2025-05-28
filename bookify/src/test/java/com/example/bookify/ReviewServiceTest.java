package com.example.bookify;

import com.example.bookify.dto.ReviewDTO;
import com.example.bookify.model.Booking;
import com.example.bookify.model.Review;
import com.example.bookify.repository.BookingRepository;
import com.example.bookify.repository.ReviewRepository;
import com.example.bookify.service.ReviewService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.*;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ReviewServiceTest {

    @Mock
    private ReviewRepository repo;

    @Mock
    private BookingRepository bookingRepo;

    @InjectMocks
    private ReviewService reviewService;

    private Booking booking;

    @BeforeEach
    void setUp() {
        booking = new Booking();
        booking.setId(10L);
    }

    @Test
    void create_WithValidBooking_SavesReview() {
        ReviewDTO dto = new ReviewDTO();
        dto.setBookingId(10L);
        dto.setRating(4);
        dto.setComment("Great!");

        when(bookingRepo.findById(10L)).thenReturn(Optional.of(booking));
        ArgumentCaptor<Review> captor = ArgumentCaptor.forClass(Review.class);
        when(repo.save(any(Review.class))).thenAnswer(i -> i.getArgument(0));

        Review result = reviewService.create(dto);

        verify(repo).save(captor.capture());
        Review saved = captor.getValue();
        assertSame(booking, saved.getBooking());
        assertEquals(4, saved.getRating());
        assertEquals("Great!", saved.getComment());
        assertNotNull(saved.getCreatedAt());
        assertFalse(saved.isModerated());
        assertNull(saved.getModerationRemarks());
        assertSame(saved, result);
    }

    @Test
    void create_WhenBookingNotFound_Throws() {
        when(bookingRepo.findById(99L)).thenReturn(Optional.empty());
        ReviewDTO dto = new ReviewDTO();
        dto.setBookingId(99L);

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> reviewService.create(dto)
        );
        assertTrue(ex.getMessage().contains("Booking not found: 99"));
        verifyNoInteractions(repo);
    }

    @Test
    void getById_Existing_ReturnsReview() {
        Review r = new Review();
        r.setId(5L);
        when(repo.findById(5L)).thenReturn(Optional.of(r));

        Review found = reviewService.getById(5L);
        assertSame(r, found);
    }

    @Test
    void getById_NotFound_Throws() {
        when(repo.findById(6L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> reviewService.getById(6L)
        );
        assertTrue(ex.getMessage().contains("Review not found: 6"));
    }

    @Test
    void getByBookingId_Delegates() {
        reviewService.getByBookingId(10L);
        verify(repo).findByBooking_Id(10L);
    }

    @Test
    void getByListing_Delegates() {
        reviewService.getByListing(7L);
        verify(repo).findByBooking_Listing_IdAndModeratedTrue(7L);
    }

    @Test
    void getPending_Delegates() {
        reviewService.getPending();
        verify(repo).findByModeratedFalseAndModerationRemarksIsNull();
    }

    @Test
    void moderate_WithExisting_SavesModeration() {
        Review r = new Review();
        r.setId(8L);
        when(repo.findById(8L)).thenReturn(Optional.of(r));
        when(repo.save(any(Review.class))).thenAnswer(i -> i.getArgument(0));

        Review out = reviewService.moderate(8L, true, "Looks good");

        assertTrue(out.isModerated());
        assertEquals("Looks good", out.getModerationRemarks());
        verify(repo).save(r);
    }

    @Test
    void moderate_WhenNotFound_ThrowsEntityNotFound() {
        when(repo.findById(9L)).thenReturn(Optional.empty());

        assertThrows(
                EntityNotFoundException.class,
                () -> reviewService.moderate(9L, true, "x")
        );
    }
}
