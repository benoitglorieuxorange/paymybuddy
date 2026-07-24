package com.globe.paymybuddy.services;

import com.globe.paymybuddy.dtos.ChangePasswordDto;
import com.globe.paymybuddy.dtos.ProfilResponseDto;
import com.globe.paymybuddy.exceptions.InvalidPasswordException;
import com.globe.paymybuddy.exceptions.UserNotFoundException;
import com.globe.paymybuddy.models.User;
import com.globe.paymybuddy.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfilServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ProfilService profilService;

    private User user;
    private static final Long USER_ID = 1L;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(USER_ID);
        user.setUsername("Benoit GLORIEUX");
        user.setEmail("john.doe@example.com");
        user.setBalance(BigDecimal.valueOf(100));
        user.setPassword("encodedOldPassword");
    }

    // ---------- getProfilByUserId ----------

    @Test
    void getProfilByUserId_shouldReturnProfilResponseDto_whenUserExists() {
        // Arrange
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        // Act
        ProfilResponseDto result = profilService.getProfilByUserId(USER_ID);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.username()).isEqualTo("Benoit GLORIEUX");
        assertThat(result.email()).isEqualTo("john.doe@example.com");
        assertThat(result.balance()).isEqualByComparingTo(BigDecimal.valueOf(100));

        verify(userRepository, times(1)).findById(USER_ID);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void getProfilByUserId_shouldThrowUserNotFoundException_whenUserDoesNotExist() {
        // Arrange
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> profilService.getProfilByUserId(USER_ID))
                .isInstanceOf(UserNotFoundException.class);

        verify(userRepository, times(1)).findById(USER_ID);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(passwordEncoder);
    }

    // ---------- changePassword ----------

    @Test
    void changePassword_shouldEncodeAndSaveNewPassword_whenCurrentPasswordIsCorrect() {
        // Arrange
        ChangePasswordDto dto = new ChangePasswordDto("oldPassword", "newPassword");

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldPassword", "encodedOldPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");

        // Act
        profilService.changePassword(USER_ID, dto);

        // Assert
        assertThat(user.getPassword()).isEqualTo("encodedNewPassword");

        verify(userRepository, times(1)).findById(USER_ID);
        verify(passwordEncoder, times(1)).matches("oldPassword", "encodedOldPassword");
        verify(passwordEncoder, times(1)).encode("newPassword");
        verify(userRepository, times(1)).save(user);
        verifyNoMoreInteractions(userRepository, passwordEncoder);
    }

    @Test
    void changePassword_shouldThrowInvalidPasswordException_whenCurrentPasswordIsIncorrect() {
        // Arrange
        ChangePasswordDto dto = new ChangePasswordDto("wrongPassword", "newPassword");

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", "encodedOldPassword")).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> profilService.changePassword(USER_ID, dto))
                .isInstanceOf(InvalidPasswordException.class)
                .hasMessage("Current password is incorrect");

        // Le mot de passe ne doit pas avoir été modifié ni sauvegardé
        assertThat(user.getPassword()).isEqualTo("encodedOldPassword");
        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void changePassword_shouldThrowUserNotFoundException_whenUserDoesNotExist() {
        // Arrange
        ChangePasswordDto dto = new ChangePasswordDto("oldPassword", "newPassword");

        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> profilService.changePassword(USER_ID, dto))
                .isInstanceOf(UserNotFoundException.class);

        verify(userRepository, times(1)).findById(USER_ID);
        verifyNoInteractions(passwordEncoder);
        verify(userRepository, never()).save(any());
    }
}