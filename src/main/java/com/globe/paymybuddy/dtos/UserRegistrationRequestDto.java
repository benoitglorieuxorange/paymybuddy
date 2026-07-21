package com.globe.paymybuddy.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record UserRegistrationRequestDto(
        @NotBlank(message = "L'addresse email est obligatoire")
        @Email(message = "L'addresse email doit être valide")
        String email,
        @NotBlank(message = "Le mot de passe est obligatoire")
        @Size(min = 7, message="Le mot de passe doit avoir au minimum 7 caractères")
        String password,
        BigDecimal balance,
        String username
) {}
