package com.globe.paymybuddy.dtos;

import java.math.BigDecimal;

public record TransactionRequestDto(
        String receiverEmail,
        BigDecimal amount,
        String description
) {}