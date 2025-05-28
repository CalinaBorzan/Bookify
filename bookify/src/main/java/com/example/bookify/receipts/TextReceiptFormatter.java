package com.example.bookify.receipts;


import com.example.bookify.model.Payment;
import org.springframework.stereotype.Component;

/**
 * A component for formatting Payment objects into text-based receipts.
 */
@Component
public class TextReceiptFormatter implements ReceiptFormatter {

    @Override
    public String formatKey() { return "txt"; }

    /**
     * Formats a Payment object into a text-based receipt format.
     *
     * @param p The Payment object to format.
     * @return The formatted text representation of the Payment.
     */
    @Override
    public String format(Payment p) {
        var sb = new StringBuilder();
        sb.append("Booking ID: ").append(p.getBooking().getId()).append("\n");
        sb.append("Amount: €").append(p.getAmount()).append("\n");
        sb.append("Transaction ID: ").append(p.getTransactionId()).append("\n");
        sb.append("Paid at: ").append(p.getPaidAt()).append("\n");
        return sb.toString();
    }

    @Override
    public String contentType() { return "text/plain"; }

    @Override
    public String extension() { return "txt"; }
}
