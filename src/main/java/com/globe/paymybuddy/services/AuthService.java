package com.globe.paymybuddy.services;


import com.globe.paymybuddy.dtos.UserLoginRequestDto;
import com.globe.paymybuddy.dtos.UserRegistrationRequestDto;
import com.globe.paymybuddy.models.User;
import com.globe.paymybuddy.repositories.UserRepository;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository, JwtService jwtService, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    public String login(UserLoginRequestDto request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        User user = userRepository.findByEmail(request.email()).orElseThrow();
        return jwtService.generateToken(user);
    }

    @Transactional
    public void register(UserRegistrationRequestDto request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new RuntimeException("Email déjà utilisé");
        }
        User user = new User();
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setUsername(request.username());
        user.setBalance(request.balance());
        userRepository.save(user);
    }


}
