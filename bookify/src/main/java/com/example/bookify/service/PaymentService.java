package com.example.bookify.service;


import com.example.bookify.dto.PaymentDTO;
import com.example.bookify.model.*;
import com.example.bookify.repository.BookingRepository;
import com.example.bookify.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * PaymentService manages operations related to payments, including creation and retrieval of payment records.
 */
@Service
public class PaymentService {
    private final PaymentRepository repo;
    private final BookingRepository bookingRepo;
    private final EmailService emailService;

    /**
     * Constructs a new PaymentService instance with required dependencies.
     *
     * @param repo the PaymentRepository for accessing payment data
     * @param bookingRepo the BookingRepository for accessing booking data
     * @param emailService the EmailService for sending payment receipt notifications
     */
    public PaymentService(PaymentRepository repo,
                          BookingRepository bookingRepo,EmailService emailService) {
        this.repo = repo;
        this.bookingRepo = bookingRepo;
        this.emailService = emailService;
    }

    /**
     * Creates a new payment based on the provided DTO, associates it with a booking, and sends a payment receipt email.
     *
     * @param dto the PaymentDTO containing payment details
     * @return the created Payment entity
     * @throws RuntimeException if the associated booking is not found
     */
    @Transactional
    public Payment create(PaymentDTO dto) {
        Booking booking = bookingRepo.findById(dto.getBookingId())
                .orElseThrow(() -> new RuntimeException("Booking not found: " + dto.getBookingId()));
        Payment payment = Payment.builder()
                .booking(booking)
                .amount(dto.getAmount())
                .paidAt(LocalDateTime.now())
                .status(PaymentStatus.SUCCESSFUL)
                .transactionId(dto.getTransactionId())
                .build();

        Payment saved = repo.save(payment);
        emailService.sendPaymentReceipt(
                booking.getUser().getEmail(), saved);
        return saved;    }

    /**
     * Retrieves a payment by its unique identifier.
     *
     * @param id the unique identifier of the payment
     * @return the Payment entity associated with the given ID
     * @throws RuntimeException if no payment is found with the specified ID
     */
    public Payment getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found: " + id));
    }

    /**
     * Retrieves all payments associated with a specific booking.
     *
     * @param bookingId the unique identifier of the booking
     * @return a list of Payment entities for the specified booking
     */
    public List<Payment> getByBookingId(Long bookingId) {
        return repo.findByBookingId(bookingId);
    }
}
