package com.bsm.tradenest.services;

import com.bsm.tradenest.config.JwtUtil;
import com.bsm.tradenest.dao.Userdao;
import com.bsm.tradenest.dto.*;
import com.bsm.tradenest.enums.Role;
import com.bsm.tradenest.model.Usermodel;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class Authservice {

    private final Userdao userdao;
    private final PasswordEncoder encoder;
    private final JwtUtil jwt;

    public Authservice(Userdao userdao, PasswordEncoder encoder, JwtUtil jwt) {
        this.userdao = userdao;
        this.encoder = encoder;
        this.jwt = jwt;
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

    public LoginResponseDto login(LoginRequest req) {
        // authenticate + issue JWT (next step)
        Usermodel user = userdao.findByEmail(req.getEmail());

        if (user == null || !user.isEnabled()) {
            throw new RuntimeException("Invalid credentials");
        }

        if (!encoder.matches(req.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwt.generateToken(
                user.getEmail(),
                user.getRole().name()
        );

        return new LoginResponseDto( "Login Success",true , token, user.getRole().name());
    }



}
