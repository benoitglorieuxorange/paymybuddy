package com.globe.paymybuddy.dtos;

public record ApiErrorResponse(
        String code,
        String message
) {}
