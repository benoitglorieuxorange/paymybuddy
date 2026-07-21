package com.globe.paymybuddy.services;

import com.globe.paymybuddy.dtos.UserRegisterRequestDto;
import com.globe.paymybuddy.dtos.UserRegisterResponseDto;
import com.globe.paymybuddy.mappers.UserMapper;
import com.globe.paymybuddy.models.User;
import com.globe.paymybuddy.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRegisterServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserRegisterService userRegisterService;

    private UserRegisterRequestDto requestDto;
    private User user;
    private UserRegisterResponseDto responseDto;

    private static final String EMAIL = "benoit@paymybuddy.com";

    @BeforeEach
    void setUp() {
        requestDto = new UserRegisterRequestDto("Benoit GLORIEUX", EMAIL, "password123", BigDecimal.ZERO);

        user = new User();
        user.setUsername("Benoit GLORIEUX");
        user.setEmail(EMAIL);
        user.setPassword("encodedPassword123");
        user.setBalance(BigDecimal.ZERO);

        responseDto = new UserRegisterResponseDto("Benoit GLORIEUX", EMAIL, BigDecimal.ZERO);
    }

    @Test
    void createUser_shouldCreateAndReturnUser_whenEmailDoesNotExist() {
        // Arrange
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());
        when(userMapper.toEntity(requestDto)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(responseDto);

        // Act
        UserRegisterResponseDto result = userRegisterService.createUser(requestDto);

        // Assert
        assertThat(result).isEqualTo(responseDto);

        verify(userRepository, times(1)).findByEmail(EMAIL);
        verify(userMapper, times(1)).toEntity(requestDto);
        verify(userRepository, times(1)).save(user);
        verify(userMapper, times(1)).toDto(user);
        verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    void createUser_shouldThrowIllegalArgumentException_whenEmailAlreadyExists() {
        // Arrange
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));

        // Act & Assert
        assertThatThrownBy(() -> userRegisterService.createUser(requestDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email already exists");

        verify(userRepository, times(1)).findByEmail(EMAIL);
        verify(userMapper, never()).toEntity(any());
        verify(userRepository, never()).save(any());
        verify(userMapper, never()).toDto(any());
    }
}