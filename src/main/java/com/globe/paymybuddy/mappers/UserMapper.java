package com.globe.paymybuddy.mappers;

import com.globe.paymybuddy.dtos.UserRegisterRequestDto;
import com.globe.paymybuddy.dtos.UserRegisterResponseDto;
import com.globe.paymybuddy.models.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    // convert entity to Dto
    public UserRegisterResponseDto toDto(User user){
        if (user == null) return null;
        return new UserRegisterResponseDto(user.getUsername(), user.getEmail(),  user.getBalance());
    }

    // convert Dto to entity
    public User toEntity(UserRegisterRequestDto dto){
        if (dto == null) return null;
        User user = new User();
        user.setUsername(dto.username());
        user.setEmail(dto.email());
        user.setPassword(dto.password());
        user.setBalance(dto.balance());
        return user;
    }

    // 2. POUR LA MISE À JOUR : On prend l'user existant et on l'écrase avec le DTO
    public void updateEntityFromDto(UserRegisterRequestDto dto, User existingUser) {
        if (dto == null || existingUser == null) return;

        // On ne fait PAS de "new User()". On modifie l'objet existant !
        if (dto.username() != null){ existingUser.setUsername(dto.username()); }
        if (dto.email() != null){ existingUser.setEmail(dto.email()); }
        if (dto.password() != null){ existingUser.setPassword(dto.password()); }
        if (dto.balance() != null){ existingUser.setBalance(dto.balance()); }

        // Pas besoin de "return", l'objet existant est modifié par référence
    }



}
