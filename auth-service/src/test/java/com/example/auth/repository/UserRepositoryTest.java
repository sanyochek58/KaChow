package com.example.auth.repository;

import com.example.auth.model.Role;
import com.example.auth.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataMongoTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    public void tearDown(){
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("findByEmail - находит пользователя по email")
    void findByEmail_found(){

        // given

        User user = User.builder()
                .email("test@mail.ru")
                .password("hashed_password")
                .role(Role.CLIENT)
                .build();
        userRepository.save(user);

        // when
        Optional<User> result = userRepository.findByEmail("test@mail.ru");

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("test@mail.ru");
        assertThat(result.get().getRole()).isEqualTo(Role.CLIENT);
    }

    @Test
    @DisplayName("findByEmail - возвращает пусто если не найден")
    void findByEmail_notFound(){

        // when
        Optional<User> result = userRepository.findByEmail("test@mail.ru");

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("existsByEmail - true если занят")
    void existsByEmail_true(){
        // given
        User user = User.builder()
                .email("test@mail.ru")
                .password("hashed_password")
                .role(Role.CLIENT)
                .build();

        userRepository.save(user);

        // when / then

        assertThat(userRepository.existsByEmail("test@mail.ru")).isTrue();
    }

    @Test
    @DisplayName("existsByEmail - false если не занят")
    void existsByEmail_false(){
        assertThat(userRepository.existsByEmail("nobody@mail.ru")).isFalse();
    }

    @Test
    @DisplayName("save — сохраняет пользователя с присвоением id")
    void save_assignsId() {
        // given
        User user = User.builder()
                .email("test@mail.ru")
                .password("hashed")
                .role(Role.CLIENT)
                .build();

        // when
        User saved = userRepository.save(user);

        // then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getEmail()).isEqualTo("test@mail.ru");
    }
}
