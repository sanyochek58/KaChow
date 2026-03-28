package com.example.auth.repository;

import com.example.auth.model.RefreshToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
class RefreshTokenRepositoryTest {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @AfterEach
    void cleanUp() {
        refreshTokenRepository.deleteAll();
    }

    @Test
    @DisplayName("findByToken — находит токен")
    void findByToken_found() {
        // given
        RefreshToken token = RefreshToken.builder()
                .userId("user-id-1")
                .token("my-refresh-token")
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
        refreshTokenRepository.save(token);

        // when
        Optional<RefreshToken> result = refreshTokenRepository.findByToken("my-refresh-token");

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getUserId()).isEqualTo("user-id-1");
    }

    @Test
    @DisplayName("findByToken — возвращает пусто если не найден")
    void findByToken_notFound() {
        Optional<RefreshToken> result = refreshTokenRepository.findByToken("unknown-token");
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("deleteByToken — удаляет токен")
    void deleteByToken() {
        // given
        RefreshToken token = RefreshToken.builder()
                .userId("user-id-1")
                .token("my-refresh-token")
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
        refreshTokenRepository.save(token);

        // when
        refreshTokenRepository.deleteByToken("my-refresh-token");

        // then
        assertThat(refreshTokenRepository.findByToken("my-refresh-token")).isEmpty();
    }

    @Test
    @DisplayName("deleteByUserId — удаляет все токены пользователя")
    void deleteByUserId() {
        // given — два токена одного пользователя
        refreshTokenRepository.save(RefreshToken.builder()
                .userId("user-id-1")
                .token("token-1")
                .expiresAt(Instant.now().plusSeconds(3600))
                .build());
        refreshTokenRepository.save(RefreshToken.builder()
                .userId("user-id-1")
                .token("token-2")
                .expiresAt(Instant.now().plusSeconds(3600))
                .build());

        // when
        refreshTokenRepository.deleteByUserId("user-id-1");

        // then — оба удалены
        assertThat(refreshTokenRepository.findByToken("token-1")).isEmpty();
        assertThat(refreshTokenRepository.findByToken("token-2")).isEmpty();
    }
}