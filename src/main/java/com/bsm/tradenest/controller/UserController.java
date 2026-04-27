package com.bsm.tradenest.controller;

import com.bsm.tradenest.config.JwtUtil;
import com.bsm.tradenest.dao.Userdao;
import com.bsm.tradenest.model.Usermodel;
import com.bsm.tradenest.services.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired; // Added

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired // Added
    private JwtUtil jwtutil;
    @Autowired // Added
    private UserService service;
    @Autowired // Added
    private Userdao dao;



    @PostMapping("/switchtype")
    public ResponseEntity<String> switchType(HttpServletRequest request) { // Changed return type
        String jwt = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("access_token".equals(cookie.getName())) { // Assuming cookie name
                    jwt = cookie.getValue();
                    break;
                }
            }
        }

        if (jwt == null)
            return ResponseEntity.status(401).body("No token found");

        String email = jwtutil.extractEmail(jwt);
        service.switchUser(email); // Assuming this updates DB
        return ResponseEntity.ok("User type switched successfully");
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        String refreshToken = null;
        Cookie[] cookies = request.getCookies();

        // 1. Get Refresh Token from Cookie (Iterate properly)
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refresh_token".equals(cookie.getName())) { // Assuming cookie name
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        // Use the TOKEN from cookie, do NOT generate a new one blindly from a null
        // 'jwt'
        if (refreshToken != null && jwtutil.validateToken(refreshToken)) {

            // 2. Get username from token
            String email = jwtutil.extractEmail(refreshToken);

            // 3. Load FRESH user details from DB
            Usermodel user = dao.findByEmail(email); // Assume this exists

            // 4. Generate NEW Access Token
            // You need a method in JwtUtil to create a cookie, or do it manually here
            // Assuming generateJwtCookie creates an HttpOnly cookie string
            String newAccessToken = jwtutil.generateToken(user.getEmail(), user.getRole().name());

            ResponseCookie jwtCookie = ResponseCookie.from("access_token", newAccessToken)
                    .httpOnly(true)
                    .secure(false) // Set to true in production (HTTPS)
                    .path("/")
                    .maxAge(24 * 60 * 60)
                    .build();

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                    .body(new MessageResponse("Token Refreshed Successfully", user.getRole()));
        }

        return ResponseEntity.badRequest().body(new MessageResponse("Refresh Token is empty or invalid!"));
    }

    // Simple DTO for response
    static class MessageResponse {
        String message;
        Object data;

        public MessageResponse(String message, Object data) {
            this.message = message;
            this.data = data;
        }

        public MessageResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public Object getData() {
            return data;
        }
    }
}
