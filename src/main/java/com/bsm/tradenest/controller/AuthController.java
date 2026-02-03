package com.bsm.tradenest.controller;

import com.bsm.tradenest.dto.Authdto;
import com.bsm.tradenest.dto.LoginRequest;
import com.bsm.tradenest.dto.SignupRequest;
import com.bsm.tradenest.model.Usermodel;
import com.bsm.tradenest.services.Authservice;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final Authservice service;

    public AuthController(Authservice service) {
        this.service = service;
    }

    @PostMapping("/signup")
    public Authdto signup(@RequestBody SignupRequest req) {
        return service.signup(req);
    }

    @PostMapping("/login")
    public Authdto login(@RequestBody LoginRequest req) {
        return service.login(req);
    }
}
