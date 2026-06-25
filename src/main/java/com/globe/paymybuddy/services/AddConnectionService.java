package com.globe.paymybuddy.services;

import com.globe.paymybuddy.dtos.UserRegisterRequestDto;
import com.globe.paymybuddy.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AddConnectionService {

    private final UserRepository userRepository;

    public AddConnectionService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserRegisterRequestDto addConnection(UserRegisterRequestDto request) {
           // Implement the logic to add a connection here
        return request;
    }

}