package com.bsm.tradenest.config;

import com.bsm.tradenest.dao.Userdao;
import com.bsm.tradenest.enums.Role;
import com.bsm.tradenest.model.Usermodel;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminBootstrap implements CommandLineRunner {

    private final Userdao userdao;
    private final PasswordEncoder encoder;

    public AdminBootstrap(Userdao userdao, PasswordEncoder encoder) {
        this.userdao = userdao;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) {

        if (!userdao.adminExists()) {

            Usermodel admin = new Usermodel();
            admin.setEmail("admin@system.com");
            admin.setPassword(encoder.encode("admin123"));
            admin.setRole(Role.ROLE_ADMIN);
            admin.setEnabled(true);

            userdao.save(admin);

            System.out.println("✅ Initial ADMIN created");
        }
    }
}

