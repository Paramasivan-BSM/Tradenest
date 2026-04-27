package com.bsm.tradenest.services;

import com.bsm.tradenest.config.JwtUtil;
import com.bsm.tradenest.dao.BookingDao;
import com.bsm.tradenest.dao.WorkerDao;
import com.bsm.tradenest.enums.BookingStatus;
import com.bsm.tradenest.model.Booking;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.bson.Document;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class BookingService {

    private final BookingDao bookingDao;
    private final WorkerDao workerDao;
    private final JwtUtil jwtUtil;

    public BookingService(BookingDao bookingDao, WorkerDao workerDao, JwtUtil jwtUtil) {
        this.bookingDao = bookingDao;
        this.workerDao = workerDao;
        this.jwtUtil = jwtUtil;
    }

    public String createBooking(String userEmail, String workerName, String workerId,
            String description, double amount, String scheduledDate) {
        Booking b = new Booking();
        b.setUserEmail(userEmail);
        b.setWorkerName(workerName);
        b.setWorkerId(workerId);
        b.setDescription(description);
        b.setAmount(amount);
        b.setStatus(BookingStatus.PENDING);
        b.setScheduledDate(scheduledDate);
        b.setCreatedAt(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        // Look up the worker's email from their profile so we can query by it later
        try {
            Document workerDoc = workerDao.findByName(workerName);
            if (workerDoc != null && workerDoc.getString("email") != null) {
                b.setWorkerEmail(workerDoc.getString("email"));
            }
        } catch (Exception ignored) {
            // Don't fail the booking if worker email lookup fails
        }

        return bookingDao.save(b);
    }

    public void acceptBooking(String bookingId) {
        bookingDao.updateStatus(bookingId, BookingStatus.ACCEPTED);
    }

    public void rejectBooking(String bookingId) {
        bookingDao.updateStatus(bookingId, BookingStatus.REJECTED);
    }

    public void completeBooking(String bookingId) {
        bookingDao.updateStatus(bookingId, BookingStatus.COMPLETED);
    }

    public List<Booking> getUserBookings(String email) {
        return bookingDao.findByUserEmail(email);
    }

    /**
     * 3-tier fetch strategy for worker bookings:
     * 1. By workerId (most reliable — exact MongoDB _id match)
     * 2. By workerEmail (new bookings after email field was added)
     * 3. By workerName (legacy fallback for old records)
     */
    public List<Booking> getWorkerBookingsByEmail(String workerEmail, String workerName, String workerId) {
        if (workerId != null && !workerId.isBlank()) {
            List<Booking> byId = bookingDao.findByWorkerId(workerId);
            if (!byId.isEmpty())
                return byId;
        }
        List<Booking> byEmail = bookingDao.findByWorkerEmail(workerEmail);
        if (!byEmail.isEmpty())
            return byEmail;
        if (workerName != null && !workerName.isBlank()) {
            return bookingDao.findByWorkerName(workerName);
        }
        return byEmail;
    }

    public List<Booking> getWorkerBookings(String workerName) {
        return bookingDao.findByWorkerName(workerName);
    }

    public Booking getBookingById(String id) {
        return bookingDao.findById(id);
    }

    public String extractEmailFromRequest(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if ("access_token".equals(c.getName())) {
                    return jwtUtil.extractEmail(c.getValue());
                }
            }
        }
        return null;
    }
}
