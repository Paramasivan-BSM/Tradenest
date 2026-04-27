package com.bsm.tradenest.dto;

public record LoginResponseDto(String msg, boolean status,String token, String role) {
    @Override
    public String msg() {
        return msg;
    }

    @Override
    public boolean status() {
        return status;
    }

    @Override
    public String token() {
        return token;
    }

    @Override
    public String role() {
        return role;
    }
}
