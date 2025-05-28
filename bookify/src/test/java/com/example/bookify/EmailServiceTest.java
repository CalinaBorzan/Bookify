package com.example.bookify;

import com.example.bookify.model.*;
import com.example.bookify.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(emailService, "fromAddress", "noreply@bookify.com");
        ReflectionTestUtils.setField(emailService, "frontendUrl",   "http://app");
    }

    @Test
    void sendPasswordReset_ComposesCorrectLink() {
        emailService.sendPasswordReset("a@b.com", "tok");

        ArgumentCaptor<SimpleMailMessage> msgCap = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(msgCap.capture());

        SimpleMailMessage sent = msgCap.getValue();
        assertEquals("noreply@bookify.com", sent.getFrom());
        assertArrayEquals(new String[]{"a@b.com"}, sent.getTo());
        assertTrue(sent.getText().contains("http://app/reset-password?email=a%40b.com&token=tok"));
    }

    @Test
    void sendBookingConfirmation_IncludesAllSections() {
        // prepare user & owner
        User user = new User();
        user.setFirstName("Jane");
        user.setEmail("jane@example.com");

        User owner = new User();
        owner.setFirstName("Host");
        owner.setLastName("Guy");
        owner.setEmail("host@example.com");

        // prepare a Hotel listing
        Hotel hotel = new Hotel();
        hotel.setTitle("Hotel Foo");
        hotel.setDescription("Lovely stay");
        hotel.setPrice(BigDecimal.valueOf(150.0));
        hotel.setCreatedBy(owner);

        // build the Booking, then set its ID
        Booking booking = Booking.builder()
                .type(BookingType.HOTEL)
                .user(user)
                .listing(hotel)
                .checkIn(LocalDate.from(LocalDateTime.of(2025, 6, 1, 14, 0)))
                .checkOut(LocalDate.from(LocalDateTime.of(2025, 6, 3, 11, 0)))
                .build();
        booking.setId(5L);

        // add one successful payment, then set its ID
        Payment payment = new Payment.Builder()
                .booking(booking)
                .amount(BigDecimal.valueOf(150.0))
                .paidAt(LocalDateTime.now())
                .status(PaymentStatus.SUCCESSFUL)
                .transactionId("txn-123")
                .build();
        payment.setId(77L);
        booking.getPayments().add(payment);

        // exercise
        emailService.sendBookingConfirmation(booking);

        ArgumentCaptor<SimpleMailMessage> msgCap = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(msgCap.capture());

        SimpleMailMessage sent = msgCap.getValue();
        String body = sent.getText();

        assertEquals("noreply@bookify.com", sent.getFrom());
        assertArrayEquals(new String[]{"jane@example.com"}, sent.getTo());
        assertTrue(sent.getSubject().contains("Booking Confirmation – HOTEL #5"));

        // verify presence of key sections
        assertTrue(body.contains("Hi Jane,"));
        assertTrue(body.contains("Listing: Hotel Foo"));
        assertTrue(body.contains("Payment:"));
        assertTrue(body.contains("Status:         SUCCESSFUL"));
        assertTrue(body.contains("Host Contact:"));
        assertTrue(body.contains("Host Guy — host@example.com"));
    }

    @Test
    void sendPaymentReceipt_ComposesReceiptCorrectly() {
        // build a dummy booking with ID and user email
        Booking booking = new Booking();
        booking.setId(33L);
        User u = new User();
        u.setEmail("cust@example.com");
        booking.setUser(u);

        // build payment, then set its ID
        Payment payment = new Payment.Builder()
                .booking(booking)
                .amount(BigDecimal.valueOf(200.0))
                .paidAt(LocalDateTime.of(2025, 5, 21, 12, 0))
                .status(PaymentStatus.SUCCESSFUL)
                .transactionId("abc123")
                .build();
        payment.setId(88L);

        emailService.sendPaymentReceipt("cust@example.com", payment);

        ArgumentCaptor<SimpleMailMessage> msgCap = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(msgCap.capture());

        SimpleMailMessage sent = msgCap.getValue();
        assertEquals("noreply@bookify.com", sent.getFrom());
        assertArrayEquals(new String[]{"cust@example.com"}, sent.getTo());
        assertTrue(sent.getSubject().contains("Payment Received"));
        assertTrue(sent.getText().contains("200.0"));
        assertTrue(sent.getText().contains("booking #33"));
    }
}
