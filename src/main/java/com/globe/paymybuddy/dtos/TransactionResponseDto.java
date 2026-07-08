package com.globe.paymybuddy.dtos;

import java.math.BigDecimal;

public record TransactionResponseDto(
        String receiverEmail,
        BigDecimal amount,
        String description
) {}
