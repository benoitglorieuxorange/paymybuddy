package com.globe.paymybuddy.dtos;

public record UserRegisterResponseDto(
        String username,
        String email,
        Double balance
) {}
