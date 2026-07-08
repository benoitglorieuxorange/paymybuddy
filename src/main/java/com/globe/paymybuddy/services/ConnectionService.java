package com.globe.paymybuddy.services;

import com.globe.paymybuddy.dtos.ConnectionRequestDto;
import com.globe.paymybuddy.dtos.ConnectionResponseDto;
import com.globe.paymybuddy.mappers.ConnectionMapper;
import com.globe.paymybuddy.models.Connection;
import com.globe.paymybuddy.models.User;
import com.globe.paymybuddy.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConnectionService {

    private final ConnectionMapper connectionMapper;
    private final UserRepository userRepository;

    public ConnectionService(ConnectionMapper connectionMapper, UserRepository userRepository) {
        this.connectionMapper = connectionMapper;
        this.userRepository = userRepository;
    }

    public ConnectionResponseDto addConnection(Long userId, ConnectionRequestDto connectionRequestDto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        User connectedUser = userRepository.findByEmail(connectionRequestDto.email())
                .orElseThrow(() -> new IllegalArgumentException("Connected user not found"));

        if (user.getId().equals(connectedUser.getId())) {
            throw new IllegalArgumentException("You can not add connection with yourself");
        }

        boolean alreadyConnected = user.getConnections().stream()
                .anyMatch(c -> c.getConnectedUser().getId().equals(connectedUser.getId()));

        if (alreadyConnected) {
            throw new RuntimeException("Cet utilisateur fait déjà partie de vos connexions.");
        }

        Connection newConnection = new Connection(user, connectedUser);
        user.getConnections().add(newConnection);

        // On sauvegarde l'user, le cascade s'occupe de la connexion
        userRepository.save(user);

        // On transforme l'entité Connection en DTO de réponse
        return connectionMapper.toResponseDto(newConnection);
    }


    public List<ConnectionResponseDto> getAllConnections(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return user.getConnections().stream()
                .map(connectionMapper::toResponseDto)
                .toList();
    }

    public void deleteConnection(Long userId, Long connectionId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Connection connection = user.getConnections().stream()
                .filter(c -> c.getId().equals(connectionId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Connection not found"));

        user.getConnections().remove(connection);
        userRepository.save(user);

    }

}