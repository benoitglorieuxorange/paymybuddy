package com.globe.paymybuddy.dtos;

import java.math.BigDecimal;

public record ProfilResponseDto(
        String username,
        String email,
        BigDecimal balance
) {}
