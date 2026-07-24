package com.globe.paymybuddy.services;

import com.globe.paymybuddy.dtos.ConnectionRequestDto;
import com.globe.paymybuddy.dtos.ConnectionResponseDto;
import com.globe.paymybuddy.mappers.ConnectionMapper;
import com.globe.paymybuddy.models.Connection;
import com.globe.paymybuddy.models.User;
import com.globe.paymybuddy.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConnectionServiceTest {

    @Mock
    private ConnectionMapper connectionMapper;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ConnectionService connectionService;

    @Test
    void addConnectionShouldCreateConnectionWhenDataIsValid() {
        Long userId = 1L;
        ConnectionRequestDto requestDto = new ConnectionRequestDto("receiver@paymybuddy.com");
        User user = userWithIdEmailAndName(1L, "sender@paymybuddy.com", "sender");
        User connectedUser = userWithIdEmailAndName(2L, "receiver@paymybuddy.com", "Receiver");
        ConnectionResponseDto expectedDto = new ConnectionResponseDto(10L, "Receiver", "receiver@paymybuddy.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail(requestDto.email())).thenReturn(Optional.of(connectedUser));
        when(connectionMapper.toResponseDto(any(Connection.class))).thenReturn(expectedDto);

        ConnectionResponseDto result = connectionService.addConnection(userId, requestDto);

        assertEquals(expectedDto, result);
        assertEquals(1, user.getConnections().size());
        assertEquals(connectedUser, user.getConnections().get(0).getConnectedUser());

        verify(userRepository).findById(userId);
        verify(userRepository).findByEmail(requestDto.email());
        verify(userRepository).save(user);

        ArgumentCaptor<Connection> captor = ArgumentCaptor.forClass(Connection.class);
        verify(connectionMapper).toResponseDto(captor.capture());
        assertEquals(user, captor.getValue().getUser());
        assertEquals(connectedUser, captor.getValue().getConnectedUser());
    }

    @Test
    void addConnectionShouldThrowWhenUserNotFound() {
        Long userId = 42L;
        ConnectionRequestDto requestDto = new ConnectionRequestDto("receiver@paymybuddy.com");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> connectionService.addConnection(userId, requestDto));

        assertEquals("User not found", exception.getMessage());
        verify(userRepository).findById(userId);
        verify(userRepository, never()).findByEmail(requestDto.email());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void addConnectionShouldThrowWhenConnectedUserNotFound() {
        Long userId = 1L;
        ConnectionRequestDto requestDto = new ConnectionRequestDto("unknown@paymybuddy.com");
        User user = userWithIdEmailAndName(1L, "sender@paymybuddy.com", "Sender");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail(requestDto.email())).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> connectionService.addConnection(userId, requestDto));

        assertEquals("Connected user not found", exception.getMessage());
        verify(userRepository).findById(userId);
        verify(userRepository).findByEmail(requestDto.email());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void addConnectionShouldThrowWhenAddingSelf() {
        Long userId = 1L;
        ConnectionRequestDto requestDto = new ConnectionRequestDto("sender@paymybuddy.com");
        User user = userWithIdEmailAndName(1L, "sender@paymybuddy.com", "Sender");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail(requestDto.email())).thenReturn(Optional.of(user));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> connectionService.addConnection(userId, requestDto));

        assertEquals("You can not add connection with yourself", exception.getMessage());
        verify(userRepository).findById(userId);
        verify(userRepository).findByEmail(requestDto.email());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void addConnectionShouldThrowWhenConnectionAlreadyExists() {
        Long userId = 1L;
        ConnectionRequestDto requestDto = new ConnectionRequestDto("receiver@paymybuddy.com");
        User user = userWithIdEmailAndName(1L, "sender@paymybuddy.com", "Sender");
        User connectedUser = userWithIdEmailAndName(2L, "receiver@paymybuddy.com", "Receiver");
        user.getConnections().add(new Connection(user, connectedUser));

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail(requestDto.email())).thenReturn(Optional.of(connectedUser));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> connectionService.addConnection(userId, requestDto));

        assertEquals("Cet utilisateur fait déjà partie de vos connexions.", exception.getMessage());
        verify(userRepository).findById(userId);
        verify(userRepository).findByEmail(requestDto.email());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getAllConnectionsShouldReturnMappedDtos() {
        Long userId = 1L;
        User sender = userWithIdEmailAndName(1L, "sender@paymybuddy.com", "Sender");
        User receiver = userWithIdEmailAndName(2L, "receiver@paymybuddy.com", "Receiver");

        Connection connection = new Connection(sender, receiver);
        sender.getConnections().add(connection);

        ConnectionResponseDto responseDto = new ConnectionResponseDto(7L, "Receiver", "receiver@paymybuddy.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(sender));
        when(connectionMapper.toResponseDto(connection)).thenReturn(responseDto);

        List<ConnectionResponseDto> result = connectionService.getAllConnections(userId);

        assertEquals(1, result.size());
        assertEquals(responseDto, result.get(0));
        verify(userRepository).findById(userId);
        verify(connectionMapper).toResponseDto(connection);
    }

    @Test
    void getAllConnectionsShouldThrowWhenUserNotFound() {
        Long userId = 123L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> connectionService.getAllConnections(userId));

        assertEquals("User not found", exception.getMessage());
        verify(userRepository).findById(userId);
    }

    @Test
    void deleteConnectionShouldRemoveConnectionAndSaveUser() {
        Long userId = 1L;
        Long connectionId = 99L;
        User sender = userWithIdEmailAndName(1L, "sender@paymybuddy.com", "sender");

        Connection connection = org.mockito.Mockito.mock(Connection.class);
        when(connection.getId()).thenReturn(connectionId);
        sender.getConnections().add(connection);

        when(userRepository.findById(userId)).thenReturn(Optional.of(sender));

        connectionService.deleteConnection(userId, connectionId);

        assertTrue(sender.getConnections().isEmpty());
        verify(userRepository).findById(userId);
        verify(userRepository).save(sender);
    }

    @Test
    void deleteConnectionShouldThrowWhenUserNotFound() {
        Long userId = 77L;
        Long connectionId = 10L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> connectionService.deleteConnection(userId, connectionId));

        assertEquals("User not found", exception.getMessage());
        verify(userRepository).findById(userId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteConnectionShouldThrowWhenConnectionNotFound() {
        Long userId = 1L;
        Long connectionId = 10L;
        User sender = userWithIdEmailAndName(1L, "sender@paymybuddy.com", "sender");

        when(userRepository.findById(userId)).thenReturn(Optional.of(sender));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> connectionService.deleteConnection(userId, connectionId));

        assertEquals("Connection not found", exception.getMessage());
        verify(userRepository).findById(userId);
        verify(userRepository, never()).save(any(User.class));
    }

    private User userWithIdEmailAndName(Long id, String email, String username) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setUsername(username);
        return user;
    }
}

