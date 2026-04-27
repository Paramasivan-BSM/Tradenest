package com.bsm.tradenest.controller;

import com.bsm.tradenest.model.Booking;
import com.bsm.tradenest.services.BookingService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createBooking(@RequestBody Map<String, Object> req,
            HttpServletRequest request) {
        String userEmail = bookingService.extractEmailFromRequest(request);
        if (userEmail == null)
            return ResponseEntity.status(401).body("Unauthorized");

        String workerName = (String) req.get("workerName");
        String workerId = (String) req.getOrDefault("workerId", "");
        String description = (String) req.get("description");
        String scheduledDate = (String) req.get("scheduledDate");
        double amount = Double.parseDouble(req.get("amount").toString());

        String bookingId = bookingService.createBooking(userEmail, workerName, workerId,
                description, amount, scheduledDate);

        return ResponseEntity.ok(Map.of("bookingId", bookingId, "message", "Booking created successfully"));
    }

    @GetMapping("/my")
    public ResponseEntity<List<Booking>> getMyBookings(HttpServletRequest request) {
        String email = bookingService.extractEmailFromRequest(request);
        if (email == null)
            return ResponseEntity.status(401).build();
        return ResponseEntity.ok(bookingService.getUserBookings(email));
    }
}
