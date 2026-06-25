package com.globe.paymybuddy.dtos;

public record ConnectionResponseDto(
   Long ConnectionId,
   String connectedUserName,
   String connectedUserEmail
) {}
