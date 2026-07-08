package com.globe.paymybuddy.controllers;

import com.globe.paymybuddy.dtos.ConnectionRequestDto;
import com.globe.paymybuddy.dtos.ConnectionResponseDto;
import com.globe.paymybuddy.services.ConnectionService;
import com.globe.paymybuddy.services.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/connections")
public class ConnectionController {

    private final ConnectionService connectionService;
    private final JwtService jwtService;

    public ConnectionController(ConnectionService connectionService, JwtService jwtService) {
        this.connectionService = connectionService;
        this.jwtService = jwtService;
    }

    @PostMapping
    public ResponseEntity<ConnectionResponseDto> createConnection(
            @Valid @RequestBody ConnectionRequestDto requestDto,
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7);
        Long senderId = jwtService.extractUserId(token); //recupere l'id du sender dans le token

        ConnectionResponseDto response = connectionService.addConnection(senderId, requestDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    @DeleteMapping("/{connectionId}")
    public ResponseEntity<Void> deleteConnection(
            @PathVariable Long connectionId,
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7);
        Long userId = jwtService.extractUserId(token);

        connectionService.deleteConnection(userId, connectionId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping
    public ResponseEntity<List<ConnectionResponseDto>> getAllConnections(
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7);
        Long userId = jwtService.extractUserId(token);

        List<ConnectionResponseDto> connections = connectionService.getAllConnections(userId);
        return new ResponseEntity<>(connections, HttpStatus.OK);
    }
}