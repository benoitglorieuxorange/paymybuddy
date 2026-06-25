package com.globe.paymybuddy.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ConnectionRequestDto(
        @NotBlank(message = "L'email est obligatoire")
        @Email(message = "Le format de l'adresse email est invalide")
        String email
) {}
