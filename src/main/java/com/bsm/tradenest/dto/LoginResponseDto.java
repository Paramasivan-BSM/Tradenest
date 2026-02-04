package com.bsm.tradenest.dto;

public record LoginResponseDto(String msg, boolean status,String token, String role) {
}
