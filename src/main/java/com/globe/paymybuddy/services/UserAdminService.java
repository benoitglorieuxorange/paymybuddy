package com.globe.paymybuddy.services;

import com.globe.paymybuddy.dtos.UserRegisterRequestDto;
import com.globe.paymybuddy.dtos.UserRegisterResponseDto;
import com.globe.paymybuddy.mappers.UserMapper;
import com.globe.paymybuddy.models.User;
import com.globe.paymybuddy.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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
//                    user.setUsername(userRegisterRequestDto.userName());            // Pour le user correspondant a l'id on modifie (map( ce même user avec les arguments suivants:
//                    user.setEmail(userRegisterRequestDto.email());                  //      user.setEmail = l'email recu en parametre = email dans userRequestDto
//                    user.setPassword(userRegisterRequestDto.password());            //      etc ...
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
        return userRepository.findById(id) // 1. On récupère le user existant (avec son ID, etc.)
                .map(existingUser -> {

                    // 2. Le mapper prend les infos du DTO et les injecte DANS le user existant
                    userMapper.updateEntityFromDto(dto, existingUser);

                    // 3. On sauvegarde ce user existant mis à jour
                    User updatedUser = userRepository.save(existingUser);

                    // 4. On renvoie le DTO de réponse
                    return userMapper.toDto(updatedUser); // userMapper.toDto(updatedUser) convertit l'entité User mise à jour en un DTO de réponse UserRegisterResponseDto
                })
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

//    public UserRegisterResponseDto registerUser(UserRegisterRequestDto dto) {
//        return userRepository.findAll()
//                .stream()
//                .filter(user -> user.getEmail().equals(dto.email()))
//                .findFirst()
//                .map(existingUser -> {
//                    throw new RuntimeException("User with email " + dto.email() + " already exists");
//                })
//                .orElseGet(() -> {
//                    User newUser = userMapper.toEntity(dto);
//                    User savedUser = userRepository.save(newUser);
//                    return userMapper.toDto(savedUser);
//                });

    public List<UserRegisterResponseDto> getAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .toList();
    }




}



