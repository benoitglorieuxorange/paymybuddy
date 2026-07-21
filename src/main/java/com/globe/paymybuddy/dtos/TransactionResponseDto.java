package com.globe.paymybuddy.dtos;

import java.math.BigDecimal;

public record TransactionResponseDto(
        Integer id,
        String receiverEmail,
        BigDecimal amount,
        String description
) {}
