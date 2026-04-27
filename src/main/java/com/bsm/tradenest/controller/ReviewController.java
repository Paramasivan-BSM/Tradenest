package com.bsm.tradenest.controller;

import com.bsm.tradenest.dao.ReviewDao;
import com.bsm.tradenest.model.Review;
import com.bsm.tradenest.services.BookingService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewDao reviewDao;
    private final BookingService bookingService; // reuse extractEmailFromRequest

    public ReviewController(ReviewDao reviewDao, BookingService bookingService) {
        this.reviewDao = reviewDao;
        this.bookingService = bookingService;
    }

    /** GET /api/reviews/{workerId} — public */
    @GetMapping("/{workerId}")
    public ResponseEntity<List<Review>> getReviews(@PathVariable String workerId) {
        return ResponseEntity.ok(reviewDao.findByWorkerId(workerId));
    }

    /** POST /api/reviews — ROLE_USER only */
    @PostMapping
    public ResponseEntity<?> postReview(@RequestBody Map<String, Object> req,
            HttpServletRequest request) {
        String email = bookingService.extractEmailFromRequest(request);
        if (email == null)
            return ResponseEntity.status(401).body("Unauthorized");

        String workerId = (String) req.get("workerId");
        String comment = (String) req.get("comment");
        int rating = Integer.parseInt(req.getOrDefault("rating", 5).toString());
        String userName = (String) req.getOrDefault("userName", email.split("@")[0]);

        if (workerId == null || workerId.isBlank())
            return ResponseEntity.badRequest().body("workerId is required");

        if (rating < 1 || rating > 5)
            return ResponseEntity.badRequest().body("Rating must be 1–5");

        if (reviewDao.hasReviewed(workerId, email))
            return ResponseEntity.badRequest().body("You have already reviewed this worker");

        Review review = new Review();
        review.setWorkerId(workerId);
        review.setUserEmail(email);
        review.setUserName(userName);
        review.setRating(rating);
        review.setComment(comment);

        reviewDao.save(review);
        return ResponseEntity.ok(Map.of("message", "Review submitted successfully"));
    }

    /**
     * PATCH /api/reviews/{reviewId}/reply — ROLE_WORKER only
     * Worker replies to a review left on their profile.
     */
    @PatchMapping("/{reviewId}/reply")
    public ResponseEntity<?> replyToReview(
            @PathVariable String reviewId,
            @RequestBody Map<String, String> body,
            HttpServletRequest request) {

        String email = bookingService.extractEmailFromRequest(request);
        if (email == null)
            return ResponseEntity.status(401).body("Unauthorized");

        String reply = body.get("reply");
        if (reply == null || reply.isBlank())
            return ResponseEntity.badRequest().body("Reply text is required");

        boolean updated = reviewDao.addReply(reviewId, reply.trim());
        if (!updated)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok(Map.of("message", "Reply posted successfully"));
    }
}
