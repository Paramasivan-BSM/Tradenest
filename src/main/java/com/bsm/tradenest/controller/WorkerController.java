package com.bsm.tradenest.controller;

import com.bsm.tradenest.config.JwtUtil;
import com.bsm.tradenest.dto.RegisterWorkerRequest;
import com.bsm.tradenest.services.WorkerService;
import com.bsm.tradenest.dao.WorkerDao;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.bson.Document;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/worker")
public class WorkerController {

    private final WorkerService service;
    private final WorkerDao workerDao;
    private final JwtUtil jwtUtil;

    public WorkerController(WorkerService service, WorkerDao workerDao, JwtUtil jwtUtil) {
        this.service = service;
        this.workerDao = workerDao;
        this.jwtUtil = jwtUtil;
    }

    /** POST /api/worker/register — Create a new worker profile */
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterWorkerRequest req,
            HttpServletRequest request) {
        String email = extractEmail(request);
        if (email == null)
            return ResponseEntity.status(401).body("Unauthorized");
        try {
            service.registerWorker(
                    req.name, req.skill,
                    req.lat != null ? req.lat : 0.0,
                    req.lng != null ? req.lng : 0.0,
                    req.radiusKm,
                    email, req.ratePerHour, req.experienceYears, req.bio, req.available);
            return ResponseEntity.ok("Worker registered");
        } catch (com.bsm.tradenest.exception.BadRequestException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /** GET /api/worker/me — Fetch the logged-in worker's own profile */
    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile(HttpServletRequest request) {
        String email = extractEmail(request);
        if (email == null)
            return ResponseEntity.status(401).body("Unauthorized");

        Document doc = workerDao.findByEmail(email);
        if (doc == null)
            return ResponseEntity.status(404).body("No worker profile found");

        // Build a clean response map
        Map<String, Object> profile = new LinkedHashMap<>();
        profile.put("id", doc.getObjectId("_id").toHexString());
        profile.put("name", doc.getString("name"));
        profile.put("skill", doc.getString("skill"));
        profile.put("bio", doc.getString("bio"));
        profile.put("available", doc.getBoolean("available", true));
        profile.put("serviceRadiusKm", doc.getDouble("serviceRadiusKm"));
        profile.put("ratePerHour", doc.getDouble("ratePerHour"));
        profile.put("experienceYears", doc.getInteger("experienceYears", 0));
        profile.put("email", email);

        Document loc = doc.get("location", Document.class);
        if (loc != null) {
            java.util.List<?> coords = loc.getList("coordinates", Number.class);
            if (coords != null && coords.size() == 2) {
                profile.put("lng", ((Number) coords.get(0)).doubleValue());
                profile.put("lat", ((Number) coords.get(1)).doubleValue());
            }
        }
        return ResponseEntity.ok(profile);
    }

    /** PUT /api/worker/update — Edit the logged-in worker's profile */
    @PutMapping("/update")
    public ResponseEntity<String> update(@RequestBody RegisterWorkerRequest req,
            HttpServletRequest request) {
        String email = extractEmail(request);
        if (email == null)
            return ResponseEntity.status(401).body("Unauthorized");
        try {
            service.updateWorker(email, req.name, req.skill,
                    req.lat != null ? req.lat : 0.0,
                    req.lng != null ? req.lng : 0.0,
                    req.radiusKm, req.ratePerHour, req.experienceYears, req.bio, req.available);
            return ResponseEntity.ok("Profile updated");
        } catch (com.bsm.tradenest.exception.BadRequestException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /** PATCH /api/worker/availability — Toggle availability without a full profile update */
    @PatchMapping("/availability")
    public ResponseEntity<String> setAvailability(
            @RequestBody java.util.Map<String, Boolean> body,
            HttpServletRequest request) {
        String email = extractEmail(request);
        if (email == null)
            return ResponseEntity.status(401).body("Unauthorized");

        Boolean available = body.get("available");
        if (available == null)
            return ResponseEntity.badRequest().body("Missing 'available' field");

        workerDao.setAvailability(email, available);
        return ResponseEntity.ok(available ? "You are now available" : "You are now unavailable");
    }

    private String extractEmail(HttpServletRequest request) {
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
