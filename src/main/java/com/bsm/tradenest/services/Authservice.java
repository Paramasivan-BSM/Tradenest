package com.bsm.tradenest.services;

import com.bsm.tradenest.dao.Userdao;
import com.bsm.tradenest.dto.AdminCreateUserRequest;
import com.bsm.tradenest.dto.Authdto;
import com.bsm.tradenest.dto.LoginRequest;
import com.bsm.tradenest.dto.SignupRequest;
import com.bsm.tradenest.enums.Role;
import com.bsm.tradenest.model.Usermodel;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class Authservice {

    private final Userdao userdao;
    private final PasswordEncoder encoder;

    public Authservice(Userdao userdao, PasswordEncoder encoder) {
        this.userdao = userdao;
        this.encoder = encoder;
    }

    // PUBLIC SIGNUP
    public Authdto signup(SignupRequest req) {

        Role role = switch (req.userType) {
            case "WORKER" -> Role.ROLE_WORKER;
            default -> Role.ROLE_USER;
        };

        Usermodel user = new Usermodel();
        user.setUsername(req.username);
        user.setEmail(req.email);
        user.setPassword(encoder.encode(req.password));
        user.setRole(role);

        userdao.save(user);
        return new Authdto("Signup successful", true);
    }

    // ADMIN CREATES USER
    public Authdto createByAdmin(AdminCreateUserRequest req) {

        Usermodel user = new Usermodel();
        user.setUsername(req.username);
        user.setEmail(req.email);
        user.setPassword(encoder.encode(req.password));
        user.setRole(req.role);

        userdao.save(user);
        return new Authdto("User created by admin", true);
    }

    public Authdto login(LoginRequest req) {
        // authenticate + issue JWT (next step)
        return new Authdto("Login success", true);
    }
}
