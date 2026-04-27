package com.bsm.tradenest.controller;

import com.bsm.tradenest.config.JwtUtil;
import com.bsm.tradenest.dto.Authdto;
import com.bsm.tradenest.dto.LoginRequest;
import com.bsm.tradenest.dto.LoginResponseDto;
import com.bsm.tradenest.dto.SignupRequest;
import com.bsm.tradenest.services.Authservice;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtUtil jwtUtil;
    private Authservice service;

    public AuthController(JwtUtil jwtUtil, Authservice service) {
        this.jwtUtil = jwtUtil;
        this.service = service;
    }

    @PostMapping("/signup")
    public Authdto signup(@RequestBody SignupRequest req) {
        return service.signup(req);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest req,
            HttpServletResponse response) {
        LoginResponseDto loginResponse = service.login(req);

        // 1. Set ACCESS Token Cookie
        ResponseCookie accessCookie = ResponseCookie.from("access_token", loginResponse.token())
                .httpOnly(true)
                .secure(false) // MUST be false on localhost
                .sameSite("Lax") // Correct for localhost
                .path("/")
                .maxAge(Duration.ofDays(1))
                .build();

        // 2. Set REFRESH Token Cookie (NEW)
        // Assuming loginResponse doesn't have refresh token, we generate it here
        // OR service.login should return it.
        // For now, generating it here since we have jwtUtil and email (req.email)
        String refreshToken = jwtUtil.generateRefreshToken(req.getEmail());

        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/") // Must be available to /api/user/refresh
                .maxAge(Duration.ofDays(7)) // 7 Days
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        return ResponseEntity.ok(
                Map.of(
                        "msg", loginResponse.msg(),
                        "status", loginResponse.status(),
                        "role", loginResponse.role()));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {

        // Clear Access Token
        Cookie cookie = new Cookie("access_token", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0);

        // Clear Refresh Token (NEW)
        Cookie refreshCookie = new Cookie("refresh_token", null);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(false);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(0);

        response.addCookie(cookie);
        response.addCookie(refreshCookie); // Add both

        return ResponseEntity.ok(
                Map.of("msg", "Logged out"));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication authentication) {

        if (authentication == null) {
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.ok(
                Map.of(
                        "email", authentication.getName(),
                        "role", authentication.getAuthorities()
                                .iterator()
                                .next()
                                .getAuthority()));
    }

}
