package com.globe.paymybuddy.services;

import com.globe.paymybuddy.dtos.UserRegisterRequestDto;
import com.globe.paymybuddy.dtos.UserRegisterResponseDto;
import com.globe.paymybuddy.mappers.UserMapper;
import com.globe.paymybuddy.models.User;
import com.globe.paymybuddy.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserRegisterService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserRegisterService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }


    // User Registration

    public UserRegisterResponseDto createUser(UserRegisterRequestDto request) {

        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }
        User user = userMapper.toEntity(request);
        userRepository.save(user);
        return userMapper.toDto(user);
    }
}
