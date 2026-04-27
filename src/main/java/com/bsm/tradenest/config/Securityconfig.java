package com.bsm.tradenest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class Securityconfig {

    @Bean
    public PasswordEncoder passwordEncrypt() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthFilter jwtFilter) throws Exception {

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/recommend", "/api/recommend/**")
                        .hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/reviews/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/portfolio/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/reviews/**").hasRole("USER")
                        .requestMatchers(HttpMethod.PATCH, "/api/reviews/*/reply").hasRole("WORKER")
                        .requestMatchers("/api/messages/**").authenticated()
                        .requestMatchers("/api/portfolio/**").hasRole("WORKER")
                        .requestMatchers("/api/user/**").hasRole("USER")
                        .requestMatchers("/api/bookings/**").hasRole("USER")
                        .requestMatchers("/api/payments/**").hasRole("USER")

                        // Worker Registration & Profile checking
                        .requestMatchers("/api/worker/register").authenticated()
                        .requestMatchers("/api/worker/me").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/api/worker/availability").hasRole("WORKER")
                        .requestMatchers("/api/worker/**").hasRole("WORKER")

                        .anyRequest().authenticated())
                .addFilterBefore(jwtFilter,
                        UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);

        config.setAllowedOrigins(List.of("http://localhost:5173"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

}
