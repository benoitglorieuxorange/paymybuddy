package com.globe.paymybuddy.controllers;

import com.globe.paymybuddy.dtos.ChangePasswordDto;
import com.globe.paymybuddy.dtos.ProfilResponseDto;

import com.globe.paymybuddy.models.User;
import com.globe.paymybuddy.services.JwtService;
import com.globe.paymybuddy.services.ProfilService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profil")
public class ProfilController {

    private final ProfilService profilService;
    private final JwtService jwtService;

    public ProfilController(ProfilService profilService, JwtService jwtService) {
        this.profilService = profilService;
        this.jwtService = jwtService;
    }

    @GetMapping
    public ResponseEntity<ProfilResponseDto> getMyProfil(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        ProfilResponseDto profil = profilService.getProfilByUserId(user.getId());
        return ResponseEntity.ok(profil);
    }

//    public ResponseEntity<ProfilResponseDto> getProfilBySenderId(@RequestHeader("Authorization") String authHeader) {
//        String token = authHeader.substring(7); // enlève "Bearer "
//        Long userId = jwtService.extractUserId(token);
//
//        ProfilResponseDto profil = profilService.getProfilBySenderId(userId);
//        return ResponseEntity.ok(profil);
//    }


    @PutMapping("/password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordDto dto,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        profilService.changePassword(user.getId(), dto);
        return ResponseEntity.noContent().build();
    }



}
