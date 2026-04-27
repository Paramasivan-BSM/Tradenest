package com.bsm.tradenest.services;

import com.bsm.tradenest.dao.BookingDao;
import com.bsm.tradenest.dao.PaymentDao;
import com.bsm.tradenest.enums.BookingStatus;
import com.bsm.tradenest.model.Booking;
import com.bsm.tradenest.model.Payment;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class PaymentService {

    private static final double ADMIN_COMMISSION_PERCENT = 2.0;

    private final PaymentDao paymentDao;
    private final BookingDao bookingDao;

    public PaymentService(PaymentDao paymentDao, BookingDao bookingDao) {
        this.paymentDao = paymentDao;
        this.bookingDao = bookingDao;
    }

    /**
     * Creates a simulated payment order (mimics Razorpay order creation).
     * Returns an orderId that the frontend uses to initiate the mock checkout.
     */
    public Payment createOrder(String bookingId) {
        Booking booking = bookingDao.findById(bookingId);
        if (booking == null)
            throw new RuntimeException("Booking not found: " + bookingId);

        double amount = booking.getAmount();
        double adminAmount = Math.round(amount * ADMIN_COMMISSION_PERCENT) / 100.0;
        double workerAmount = Math.round((amount - adminAmount) * 100.0) / 100.0;

        Payment payment = new Payment();
        payment.setBookingId(bookingId);
        payment.setOrderId("order_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16));
        payment.setPaymentId(null);
        payment.setAmount(amount);
        payment.setWorkerAmount(workerAmount);
        payment.setAdminAmount(adminAmount);
        payment.setStatus("PENDING");
        payment.setCreatedAt(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        paymentDao.save(payment);
        return payment;
    }

    /**
     * Simulates payment verification — no real API call needed.
     * In a real Razorpay integration, we'd verify HMAC signature here.
     */
    public Payment confirmPayment(String orderId) {
        Payment payment = paymentDao.findByOrderId(orderId);
        if (payment == null)
            throw new RuntimeException("Order not found: " + orderId);

        // Simulate payment ID generation (Razorpay would provide this)
        String simulatedPaymentId = "pay_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        paymentDao.updateStatusAndPaymentId(orderId, simulatedPaymentId);

        // Update booking status to PAID
        bookingDao.updateStatus(payment.getBookingId(), BookingStatus.PAID);

        payment.setPaymentId(simulatedPaymentId);
        payment.setStatus("SUCCESS");
        return payment;
    }

    public Payment getPaymentByBookingId(String bookingId) {
        return paymentDao.findByBookingId(bookingId);
    }
}
