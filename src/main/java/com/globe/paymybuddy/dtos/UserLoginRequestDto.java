package com.globe.paymybuddy.dtos;

public record UserLoginRequestDto(
        String email,
        String password
) {}
