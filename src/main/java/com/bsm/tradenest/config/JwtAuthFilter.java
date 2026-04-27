package com.bsm.tradenest.config;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();

        return (path.equals("/api/auth/login") ||
                path.equals("/api/auth/signup") ||
                "OPTIONS".equalsIgnoreCase(request.getMethod()));
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        System.out.println("Request URI: " + request.getRequestURI());
        System.out.println("Authorization: " + request.getHeader("Authorization"));
        System.out.println("Cookies: " + Arrays.toString(request.getCookies()));

        try {
            String token = extractTokenFromCookie(request);

            if (token != null) {
                String email = jwtUtil.extractEmail(token);
                String role = jwtUtil.extractRole(token);
                // Spring Security expects roles to start with ROLE_ prefix
                String authority = role.startsWith("ROLE_") ? role : "ROLE_" + role;

                System.out.println("DEBUG: Extracted role=" + role + ", authority=" + authority);

                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        email,
                        null,
                        List.of(new SimpleGrantedAuthority(authority)));

                SecurityContextHolder.getContext()
                        .setAuthentication(auth);
            }

            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException ex) {
            sendUnauthorized(response, "JWT token expired");
        } catch (Exception ex) {
            sendUnauthorized(response, "Invalid JWT token");
        }
    }

    private String extractTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null)
            return null;

        for (Cookie cookie : request.getCookies()) {
            if ("access_token".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private void sendUnauthorized(HttpServletResponse response, String message)
            throws IOException {

        SecurityContextHolder.clearContext();

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        response.getWriter().write("""
                {
                  "success": false,
                  "message": "%s"
                }
                """.formatted(message));
    }
}
