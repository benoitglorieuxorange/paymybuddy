package com.globe.paymybuddy.services;

import com.globe.paymybuddy.dtos.UserRegisterRequestDto;
import com.globe.paymybuddy.dtos.UserRegisterResponseDto;
import com.globe.paymybuddy.models.User;
import com.globe.paymybuddy.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserRegisterService {

    private final UserRepository userRepository;

    public UserRegisterService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    // User Registration

    public UserRegisterResponseDto createUser(UserRegisterRequestDto request) {

        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }
        User user = new User();
        user.setUsername(request.userName());
        user.setEmail(request.email());
        user.setPassword(request.password());
        user.setBalance(request.balance());
        userRepository.save(user);
        return new UserRegisterResponseDto(user.getEmail());
    }
}
