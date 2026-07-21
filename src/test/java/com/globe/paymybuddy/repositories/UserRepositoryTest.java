package com.globe.paymybuddy.repositories;

import com.globe.paymybuddy.models.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests d'intégration du UserRepository.
 * @DataJpaTest configure une base embarquée (H2) et ne charge que la couche JPA,
 * donc pas besoin de mocker JwtFilter ou la sécurité ici : ce test est
 * transactionnel et rollback automatiquement après chaque méthode.
 */
@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User buildUser(String email) {
        return new User("Alice", "encoded-password", email, BigDecimal.valueOf(100));
    }

    @Test
    void findByEmail_shouldReturnUser_whenEmailExists() {
        userRepository.save(buildUser("alice@example.com"));

        Optional<User> result = userRepository.findByEmail("alice@example.com");

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("alice@example.com");
        assertThat(result.get().getProfileUsername()).isEqualTo("Alice");
    }

    @Test
    void findByEmail_shouldReturnEmpty_whenEmailDoesNotExist() {
        Optional<User> result = userRepository.findByEmail("unknown@example.com");

        assertThat(result).isEmpty();
    }

    @Test
    void save_shouldPersistUserWithGeneratedId() {
        User saved = userRepository.save(buildUser("bob@example.com"));

        assertThat(saved.getId()).isNotNull();
        assertThat(userRepository.findById(saved.getId())).isPresent();
    }

    @Test
    void save_shouldThrow_whenEmailAlreadyExists() {
        userRepository.saveAndFlush(buildUser("duplicate@example.com"));

        assertThatThrownBy(() ->
                userRepository.saveAndFlush(buildUser("duplicate@example.com")))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}