package com.globe.paymybuddy.dtos;

import java.math.BigDecimal;

public record UserRegisterRequestDto(
        String username,
        String email,
        String password,
        BigDecimal balance
) {}
