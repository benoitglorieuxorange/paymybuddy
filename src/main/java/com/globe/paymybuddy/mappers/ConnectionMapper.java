package com.globe.paymybuddy.mappers;

import com.globe.paymybuddy.dtos.ConnectionResponseDto;
import com.globe.paymybuddy.models.Connection;
import org.springframework.stereotype.Component;

@Component
public class ConnectionMapper {

    public ConnectionResponseDto toResponseDto(Connection connection) {
        return new ConnectionResponseDto(
                connection.getId(),
                connection.getConnectedUser().getUsername(),
                connection.getConnectedUser().getEmail()
        );
    }
}
