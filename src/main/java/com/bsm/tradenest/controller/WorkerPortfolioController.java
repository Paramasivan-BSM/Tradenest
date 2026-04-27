package com.bsm.tradenest.controller;

import com.bsm.tradenest.dao.WorkerDao;
import com.bsm.tradenest.services.BookingService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/portfolio")
public class WorkerPortfolioController {

    private final WorkerDao workerDao;
    private final BookingService bookingService;

    public WorkerPortfolioController(WorkerDao workerDao, BookingService bookingService) {
        this.workerDao = workerDao;
        this.bookingService = bookingService;
    }

    /**
     * POST /api/portfolio/add (ROLE_WORKER)
     * Body: { "imageUrl": "https://res.cloudinary.com/..." }
     */
    @PostMapping("/add")
    public ResponseEntity<?> addImage(@RequestBody Map<String, String> req,
            HttpServletRequest request) {
        String email = bookingService.extractEmailFromRequest(request);
        if (email == null)
            return ResponseEntity.status(401).body("Unauthorized");

        String imageUrl = req.get("imageUrl");
        if (imageUrl == null || imageUrl.isBlank())
            return ResponseEntity.badRequest().body("imageUrl is required");

        workerDao.addPortfolioImage(email, imageUrl);
        return ResponseEntity.ok(Map.of("message", "Image added to portfolio"));
    }

    /**
     * GET /api/portfolio/{workerId} (public)
     * Returns list of Cloudinary image URLs for the given workerId
     */
    @GetMapping("/{workerId}")
    public ResponseEntity<List<String>> getPortfolio(@PathVariable String workerId) {
        List<String> images = workerDao.getPortfolioImages(workerId);
        return ResponseEntity.ok(images);
    }
}
