package com.bsm.tradenest.controller;

import com.bsm.tradenest.model.Payment;
import com.bsm.tradenest.services.PaymentService;
import com.bsm.tradenest.services.BookingService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final BookingService bookingService;

    public PaymentController(PaymentService paymentService, BookingService bookingService) {
        this.paymentService = paymentService;
        this.bookingService = bookingService;
    }

    /**
     * Step 1: User requests a payment order for a booking.
     * Returns an orderId that will be used to open the simulated checkout.
     */
    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestBody Map<String, String> req,
            HttpServletRequest request) {
        String email = bookingService.extractEmailFromRequest(request);
        if (email == null)
            return ResponseEntity.status(401).body("Unauthorized");

        String bookingId = req.get("bookingId");
        if (bookingId == null || bookingId.isBlank())
            return ResponseEntity.badRequest().body(Map.of("error", "bookingId is required"));

        try {
            Payment payment = paymentService.createOrder(bookingId);
            return ResponseEntity.ok(Map.of(
                    "orderId", payment.getOrderId(),
                    "amount", payment.getAmount(),
                    "bookingId", bookingId,
                    "message", "Order created successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Step 2: User confirms payment after checkout.
     * Simulates Razorpay payment verification — marks booking as PAID,
     * and records 98% worker amount + 2% admin commission.
     */
    @PostMapping("/confirm")
    public ResponseEntity<?> confirmPayment(@RequestBody Map<String, String> req,
            HttpServletRequest request) {
        String email = bookingService.extractEmailFromRequest(request);
        if (email == null)
            return ResponseEntity.status(401).body("Unauthorized");

        String orderId = req.get("orderId");
        Payment payment = paymentService.confirmPayment(orderId);

        return ResponseEntity.ok(Map.of(
                "paymentId", payment.getPaymentId(),
                "amount", payment.getAmount(),
                "workerAmount", payment.getWorkerAmount(),
                "adminCommission", payment.getAdminAmount(),
                "status", "SUCCESS",
                "message", "Payment successful! Worker will receive ₹" + payment.getWorkerAmount()));
    }

    /**
     * Get payment details for a booking (for receipt display).
     */
    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<?> getPaymentByBookingId(@PathVariable String bookingId,
            HttpServletRequest request) {
        String email = bookingService.extractEmailFromRequest(request);
        if (email == null)
            return ResponseEntity.status(401).body("Unauthorized");

        Payment payment = paymentService.getPaymentByBookingId(bookingId);
        if (payment == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(payment);
    }
}
