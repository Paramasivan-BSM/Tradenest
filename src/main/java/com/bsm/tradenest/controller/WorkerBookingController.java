package com.bsm.tradenest.controller;

import com.bsm.tradenest.dao.WorkerDao;
import com.bsm.tradenest.model.Booking;
import com.bsm.tradenest.services.BookingService;
import jakarta.servlet.http.HttpServletRequest;
import org.bson.Document;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/worker/bookings")
public class WorkerBookingController {

    private final BookingService bookingService;
    private final WorkerDao workerDao;

    public WorkerBookingController(BookingService bookingService, WorkerDao workerDao) {
        this.bookingService = bookingService;
        this.workerDao = workerDao;
    }

    /**
     * Worker sees all their incoming job requests.
     *
     * Strategy:
     * 1. Get worker email from JWT cookie.
     * 2. Look up worker profile in workersTree to get their name.
     * 3. Query bookings — primary: by workerEmail, fallback: by workerName.
     * This handles both new bookings (have workerEmail) and old bookings (only
     * workerName).
     */
    @GetMapping
    public ResponseEntity<?> getWorkerBookings(HttpServletRequest request) {
        String email = bookingService.extractEmailFromRequest(request);
        if (email == null)
            return ResponseEntity.status(401).body("Unauthorized");

        String workerName = null;
        String workerId = null;
        Document workerDoc = workerDao.findByEmail(email);
        if (workerDoc != null) {
            workerName = workerDoc.getString("name");
            if (workerDoc.getObjectId("_id") != null) {
                workerId = workerDoc.getObjectId("_id").toHexString();
            }
        }

        // 3-tier lookup: workerId → workerEmail → workerName (legacy)
        List<Booking> bookings = bookingService.getWorkerBookingsByEmail(email, workerName, workerId);
        return ResponseEntity.ok(bookings);
    }

    // Worker accepts a job
    @PostMapping("/{id}/accept")
    public ResponseEntity<?> accept(@PathVariable String id, HttpServletRequest request) {
        String email = bookingService.extractEmailFromRequest(request);
        if (email == null)
            return ResponseEntity.status(401).body("Unauthorized");
        bookingService.acceptBooking(id);
        return ResponseEntity.ok(Map.of("message", "Booking accepted"));
    }

    // Worker rejects a job
    @PostMapping("/{id}/reject")
    public ResponseEntity<?> reject(@PathVariable String id, HttpServletRequest request) {
        String email = bookingService.extractEmailFromRequest(request);
        if (email == null)
            return ResponseEntity.status(401).body("Unauthorized");
        bookingService.rejectBooking(id);
        return ResponseEntity.ok(Map.of("message", "Booking rejected"));
    }

    // Worker marks a job as complete → worker receives 98% payout
    @PostMapping("/{id}/complete")
    public ResponseEntity<?> complete(@PathVariable String id, HttpServletRequest request) {
        String email = bookingService.extractEmailFromRequest(request);
        if (email == null)
            return ResponseEntity.status(401).body("Unauthorized");
        bookingService.completeBooking(id);
        return ResponseEntity.ok(Map.of("message", "Booking completed. Earnings transferred to worker."));
    }
}
