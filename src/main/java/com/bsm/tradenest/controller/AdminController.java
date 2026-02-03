package com.bsm.tradenest.controller;

import com.bsm.tradenest.dto.AdminCreateUserRequest;
import com.bsm.tradenest.dto.Authdto;
import com.bsm.tradenest.services.Authservice;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final Authservice service;

    public AdminController(Authservice service) {
        this.service = service;
    }

    @PostMapping("/users")
    public Authdto createUser(@RequestBody AdminCreateUserRequest req) {
        return service.createByAdmin(req);
    }
}
