package com.globe.paymybuddy.services;

import com.globe.paymybuddy.dtos.UserRegisterRequestDto;
import com.globe.paymybuddy.dtos.UserRegisterResponseDto;
import com.globe.paymybuddy.mappers.UserMapper;
import com.globe.paymybuddy.models.User;
import com.globe.paymybuddy.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserAdminService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserAdminService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    // delete user
    public void deleteUser(long id) {
        userRepository.deleteById(id);
    }

    // update user
//    public UserRegisterRequestDto updateUser(long id, UserRegisterRequestDto userRegisterRequestDto) {
//        return userRepository.findById(id)
//                .map(user -> {
//                    user.setUsername(userRegisterRequestDto.userName());
//                    user.setEmail(userRegisterRequestDto.email());
//                    user.setPassword(userRegisterRequestDto.password());
//                    user.setBalance(userRegisterRequestDto.balance());
//                    return new UserRegisterRequestDto(
//                            user.getUsername(),
//                            user.getEmail(),
//                            user.getPassword(),
//                            user.getBalance()
//                    );
//                })
//                .orElse(null);
//    }

    public UserRegisterResponseDto updateUser(long id, UserRegisterRequestDto dto) {
        return userRepository.findById(id) // 1. On récupère l'user existant (avec son ID, sa date de création, etc.)
                .map(existingUser -> {

                    // 2. Le mapper prend les infos du DTO et les injecte DANS le user existant
                    userMapper.updateEntityFromDto(dto, existingUser);

                    // 3. On sauvegarde ce user existant mis à jour
                    User updatedUser = userRepository.save(existingUser);

                    // 4. On renvoie le DTO de réponse
                    return userMapper.toDto(updatedUser);
                })
                .orElseThrow(() -> new RuntimeException("User not found"));
    }



}
