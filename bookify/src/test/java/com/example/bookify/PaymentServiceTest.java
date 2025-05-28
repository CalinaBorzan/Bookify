package com.example.bookify;

import com.example.bookify.dto.PaymentDTO;
import com.example.bookify.model.*;
import com.example.bookify.repository.BookingRepository;
import com.example.bookify.repository.PaymentRepository;
import com.example.bookify.service.EmailService;
import com.example.bookify.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.*;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PaymentServiceTest {

    @Mock
    private PaymentRepository repo;

    @Mock
    private BookingRepository bookingRepo;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private PaymentService paymentService;

    private Booking booking;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("customer@example.com");

        booking = new Booking();
        booking.setId(100L);
        booking.setUser(user);
    }

    @Test
    void create_WithValidBooking_SavesPaymentAndSendsEmail() {
        // given
        PaymentDTO dto = new PaymentDTO();
        dto.setBookingId(100L);
        dto.setAmount(BigDecimal.valueOf(250.0));
        dto.setTransactionId("tx-789");

        when(bookingRepo.findById(100L))
                .thenReturn(Optional.of(booking));
        ArgumentCaptor<Payment> captor = ArgumentCaptor.forClass(Payment.class);
        when(repo.save(any(Payment.class))).thenAnswer(i -> {
            Payment p = i.getArgument(0);
            p.setId(55L);
            return p;
        });

        // when
        Payment result = paymentService.create(dto);

        // then
        verify(repo).save(captor.capture());
        Payment saved = captor.getValue();
        assertSame(booking, saved.getBooking());
        // compare BigDecimal
        assertEquals(0, BigDecimal.valueOf(250.0).compareTo(saved.getAmount()));
        assertEquals(PaymentStatus.SUCCESSFUL, saved.getStatus());
        assertEquals("tx-789", saved.getTransactionId());
        assertNotNull(saved.getPaidAt());

        // verify email sent
        verify(emailService).sendPaymentReceipt(
                eq("customer@example.com"),
                same(result)
        );

        // result should be the saved object, with id set
        assertEquals(55L, result.getId());
    }

    @Test
    void create_WhenBookingNotFound_ThrowsException() {
        PaymentDTO dto = new PaymentDTO();
        dto.setBookingId(999L);
        dto.setAmount(BigDecimal.valueOf(100.0));
        dto.setTransactionId("nope");

        when(bookingRepo.findById(999L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> paymentService.create(dto)
        );
        assertTrue(ex.getMessage().contains("Booking not found: 999"));
        verifyNoInteractions(repo, emailService);
    }

    @Test
    void getById_ExistingId_ReturnsPayment() {
        Payment p = new Payment();
        p.setId(7L);
        when(repo.findById(7L)).thenReturn(Optional.of(p));

        Payment found = paymentService.getById(7L);
        assertSame(p, found);
    }

    @Test
    void getById_NotFound_Throws() {
        when(repo.findById(8L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> paymentService.getById(8L)
        );
        assertTrue(ex.getMessage().contains("Payment not found: 8"));
    }

    @Test
    void getByBookingId_ReturnsListFromRepo() {
        Payment p1 = new Payment();
        Payment p2 = new Payment();
        when(repo.findByBookingId(100L)).thenReturn(List.of(p1, p2));

        List<Payment> list = paymentService.getByBookingId(100L);
        assertEquals(2, list.size());
        assertSame(p1, list.get(0));
        assertSame(p2, list.get(1));
        verify(repo).findByBookingId(100L);
    }
}
