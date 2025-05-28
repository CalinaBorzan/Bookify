package com.example.bookify.controller;


import com.example.bookify.dto.PaymentDTO;
import com.example.bookify.model.Payment;
import com.example.bookify.receipts.ReceiptFormatter;
import com.example.bookify.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller class for managing payment-related operations.
 * Provides endpoints for creating, retrieving, and downloading payment information.
 */
@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentService svc;
    private final Map<String, ReceiptFormatter> formatters;


    /**
     * Constructs an instance of PaymentController with the specified services and formatters.
     *
     * @param svc        The PaymentService used for payment operations.
     * @param formatters A map of receipt formatters for different formats.
     */

    public PaymentController(PaymentService svc,
                             Map<String, ReceiptFormatter> formatters) {
        this.svc = svc;
        this.formatters = formatters;
    }


    /**
     * Downloads a receipt in the specified format for a booking.
     *
     * @param bookingId The ID of the booking for which the receipt is to be downloaded.
     * @param format    The format in which the receipt should be downloaded (default is JSON).
     * @return ResponseEntity with byte array containing the receipt data, headers, and HTTP status OK.
     *         If the format is unknown, returns a bad request response.
     * @throws RuntimeException if no payment is found for the given booking ID.
     */
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/booking/{bookingId}/receipt")
    public ResponseEntity<byte[]> downloadReceipt(
            @PathVariable Long bookingId,
            @RequestParam(defaultValue="json") String format
    ) {
        ReceiptFormatter fmt = formatters.get(format);
        if (fmt == null) {
            return ResponseEntity.badRequest()
                    .body(("Unknown format: " + format).getBytes(StandardCharsets.UTF_8));
        }

        Payment payment = svc.getByBookingId(bookingId)
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No payment found"));

        String body = fmt.format(payment);
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);

        ContentDisposition cd = ContentDisposition.attachment()
                .filename("receipt_"+bookingId+"."+fmt.extension())
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(cd);
        headers.setContentType(MediaType.parseMediaType(fmt.contentType()));

        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }


    /**
     * Creates a new payment based on the provided PaymentDTO.
     *
     * @param dto The PaymentDTO object containing payment details.
     * @return ResponseEntity with HTTP status 201 CREATED and the created Payment object.
     */
    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<Payment> createPayment(@RequestBody PaymentDTO dto) {
        Payment created = svc.create(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(created);
    }

    /**
     * Retrieves a specific payment by its ID.
     *
     * @param id The ID of the payment to retrieve.
     * @return ResponseEntity with the Payment object corresponding to the provided ID.
     */

    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPayment(@PathVariable Long id) {
        Payment payment = svc.getById(id);
        return ResponseEntity.ok(payment);
    }

    /**
     * Retrieves all payments associated with a specific booking.
     *
     * @param bookingId The ID of the booking to retrieve payments for.
     * @return ResponseEntity with a list of PaymentDTO objects associated with the specified booking.
     */

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<List<PaymentDTO>> listByBooking(@PathVariable Long bookingId) {
        List<PaymentDTO> dtos = svc.getByBookingId(bookingId).stream()
                .map(p -> new PaymentDTO(
                        p.getBooking().getId(),
                        p.getAmount(),
                        p.getTransactionId()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
}

