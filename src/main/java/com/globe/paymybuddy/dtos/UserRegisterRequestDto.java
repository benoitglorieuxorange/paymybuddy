package com.globe.paymybuddy.dtos;

public record UserRegisterRequestDto(
        String userName,
        String email,
        String password,
        Double balance
) {}
