package com.bsm.tradenest.dto;

import com.bsm.tradenest.enums.Role;

public class AdminCreateUserRequest {
    public String email;
    public String password;
    public Role role;
    public String username;
}

