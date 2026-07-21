package com.globe.paymybuddy.dtos;

import java.math.BigDecimal;

public record UserRegisterResponseDto(
        String username,
        String email,
        BigDecimal balance
) {}
