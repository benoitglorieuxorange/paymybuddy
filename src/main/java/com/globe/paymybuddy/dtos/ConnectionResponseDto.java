package com.globe.paymybuddy.dtos;

public record ConnectionResponseDto(
   Long connectionId,
   String connectedUserName,
   String connectedUserEmail
) {}
