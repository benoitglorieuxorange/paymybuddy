package com.globe.paymybuddy.services;

import com.globe.paymybuddy.dtos.ChangePasswordDto;
import com.globe.paymybuddy.dtos.ProfilResponseDto;
import com.globe.paymybuddy.exceptions.InvalidPasswordException;
import com.globe.paymybuddy.models.User;
import com.globe.paymybuddy.repositories.UserRepository;
import com.globe.paymybuddy.exceptions.UserNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ProfilService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ProfilService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public ProfilResponseDto getProfilByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        return new ProfilResponseDto(user.getProfileUsername(), user.getEmail(), user.getBalance());
    }


    public void changePassword(Long userId, ChangePasswordDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (!passwordEncoder.matches(dto.currentPassword(), user.getPassword())) {
            throw new InvalidPasswordException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(dto.newPassword()));
        userRepository.save(user);
    }

}
