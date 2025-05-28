package com.example.bookify.receipts;


import com.example.bookify.model.Payment;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

/**
 * Implementation of ReceiptFormatter for JSON format.
 * Converts Payment objects to JSON strings.
 */
@Component
public class JsonReceiptFormatter implements ReceiptFormatter {
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Returns the format key for JSON, which is "json".
     *
     * @return The format key for JSON.
     */
    @Override
    public String formatKey() { return "json"; }

    /**
     * Formats a Payment object as a JSON string.
     *
     * @param p The Payment object to format.
     * @return The JSON string representation of the Payment object.
     * @throws RuntimeException If JSON formatting fails.
     */
    @Override
    public String format(Payment p) {
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(p);
        } catch (Exception e) {
            throw new RuntimeException("JSON formatting failed", e);
        }
    }

    /**
     * Returns the content type for JSON, which is "application/json".
     *
     * @return The content type for JSON.
     */
    @Override
    public String contentType() { return "application/json"; }

    /**
     * Returns the file extension for JSON files, which is "json".
     *
     * @return The file extension for JSON files.
     */
    @Override
    public String extension() { return "json"; }
}
