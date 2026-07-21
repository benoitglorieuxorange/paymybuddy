package com.globe.paymybuddy.controllers;

import com.globe.paymybuddy.dtos.UserLoginRequestDto;
import com.globe.paymybuddy.dtos.UserRegistrationRequestDto;
import com.globe.paymybuddy.services.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginRequestDto request) {
        String token = authService.login(request);
        return ResponseEntity.ok(Map.of("token", token));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegistrationRequestDto request) {
        authService.register(request);
        return ResponseEntity.ok(Map.of("message", "Compte créé"));
    }

}
