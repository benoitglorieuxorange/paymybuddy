package com.globe.paymybuddy.dtos;

public record UserRegisterRequestDto(
        String username,
        String email,
        String password,
        Double balance
) {}
