package com.example.bookify.receipts;


import com.example.bookify.model.Payment;

/**
 * Interface for defining receipt formatting operations.
 */
public interface ReceiptFormatter {
    String formatKey();
    String format(Payment payment);
    String contentType();
    String extension();
}
