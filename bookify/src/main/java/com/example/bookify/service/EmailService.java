package com.example.bookify.service;
import com.example.bookify.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.mail.SimpleMailMessage;

import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * EmailService handles sending email notifications for password resets, booking confirmations, and payment receipts.
 */
@Service
public class EmailService {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromAddress;

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;


    /**
     * Constructs a new EmailService instance with the required mail sender dependency.
     *
     * @param mailSender the JavaMailSender for sending emails
     */
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Sends a password reset email to the specified email address with a unique reset token.
     * The email contains a link to reset the password, valid for one hour.
     *
     * @param toEmail the recipient's email address
     * @param token the unique token for password reset
     */
    public void sendPasswordReset(String toEmail, String token) {

        String link = String.format(
                "%s/reset-password?email=%s&token=%s",
                frontendUrl,
                URLEncoder.encode(toEmail, StandardCharsets.UTF_8),
                token
        );

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(fromAddress);
        msg.setTo(toEmail);
        msg.setSubject("Bookify • Password reset");
        msg.setText(
                "We received a request to reset your password.\n\n" +
                        "Click the link below (or paste it into your browser) within 1 hour:\n\n" +
                        link + "\n\n" +
                        "If you didn’t request a reset, simply ignore this e-mail."
        );

        mailSender.send(msg);
    }

    /**
     * Sends a booking confirmation email to the user associated with the booking.
     * The email includes details about the booking, listing, and any associated payments, along with host contact information.
     *
     * @param booking the Booking entity containing details to include in the email
     */
    public void sendBookingConfirmation(Booking booking) {
        String to       = booking.getUser().getEmail();
        User   user     = booking.getUser();
        Listing listing = booking.getListing();
        User   owner    = listing.getCreatedBy();
        List<Payment> pays = booking.getPayments();

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(fromAddress);
        msg.setTo(to);
        msg.setSubject(String.format("Booking Confirmation – %s #%d",
                booking.getType(), booking.getId()));

        StringBuilder sb = new StringBuilder()
                .append("Hi ").append(user.getFirstName()).append(",\n\n")
                .append("Your ").append(booking.getType())
                .append(" booking #").append(booking.getId())
                .append(" is CONFIRMED.\n\n")

                // Listing header
                .append("Listing: ").append(listing.getTitle()).append("\n")
                .append("Description: ").append(listing.getDescription()).append("\n")
                .append("Price:   ").append(listing.getPrice()).append("\n");

        // Type‐specific details
        switch (booking.getType()) {
            case HOTEL -> {
                Hotel h = (Hotel) listing;
                sb.append("Check-in : ").append(booking.getCheckIn()).append('\n')
                        .append("Check-out: ").append(booking.getCheckOut()).append('\n');
            }
            case FLIGHT -> {
                Flight f = (Flight) listing;
                sb.append("Departure: ").append(f.getDeparture()).append(" @ ")
                        .append(f.getDepartureTime()).append('\n')
                        .append("Return   : ").append(f.getArrival()).append(" @ ")
                        .append(f.getArrivalTime()).append('\n');
            }
            case EVENT -> {
                Event e = (Event) listing;
                sb.append("When : ").append(e.getEventDate()).append('\n')
                        .append("Venue: ").append(e.getVenue()).append('\n');
            }
        }


        // Payment summary
        if (!pays.isEmpty()) {
            Payment p = pays.get(0);
            sb.append("\nPayment:\n")
                    .append("  Status:         ").append(p.getStatus()).append("\n")
                    .append("  Amount:         ").append(p.getAmount()).append("\n")
                    .append("  Transaction ID: ")
                    .append(p.getTransactionId() != null ? p.getTransactionId() : "N/A")
                    .append("\n");
        } else {
            sb.append("\nPayment: not yet received.\n")
                    .append("Please pay online here: [PAY NOW LINK]\n");
        }

        // Host contact
        sb.append("\nHost Contact:\n")
                .append("  ").append(owner.getFirstName())
                .append(" ").append(owner.getLastName())
                .append(" — ").append(owner.getEmail())
                .append("\n\nThanks for booking with Bookify!");

        msg.setText(sb.toString());
        mailSender.send(msg);
    }

    /**
     * Sends a payment receipt email to the specified email address.
     * The email includes details about the payment amount, booking reference, and payment date.
     *
     * @param toEmail the recipient's email address
     * @param payment the Payment entity containing payment details
     */
    public void sendPaymentReceipt(String toEmail, Payment payment) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(fromAddress);
        msg.setTo(toEmail);
        msg.setSubject("Payment Received: Ref " + payment.getId());
        msg.setText("We have received your payment of "
                + payment.getAmount() + " for booking #"
                + payment.getBooking().getId() + " on "
                + payment.getPaidAt() + ". Thank you!");
        mailSender.send(msg);
    }
}