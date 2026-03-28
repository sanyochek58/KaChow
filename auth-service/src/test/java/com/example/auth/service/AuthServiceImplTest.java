package com.example.auth.service;

import com.example.auth.dto.request.LoginRequest;
import com.example.auth.dto.request.RegisterRequest;
import com.example.auth.dto.response.AuthResponse;
import com.example.auth.exception.AuthException;
import com.example.auth.model.RefreshToken;
import com.example.auth.model.Role;
import com.example.auth.model.User;
import com.example.auth.repository.RefreshTokenRepository;
import com.example.auth.repository.UserRepository;
import com.example.auth.service.authorize.AuthServiceImpl;
import com.example.auth.service.jwt.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {

    @InjectMocks
    private AuthServiceImpl authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(authService, "refreshTokenExpiration", 604800000L);
    }

    /* ТЕСТЫ С РЕГИСТРАЦИЕЙ */

    @Test
    @DisplayName("Регистрация - успешная")
    public void register_success(){

        //given

        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@mail.ru");
        request.setPassword("12345678");
        
        when(userRepository.existsByEmail("test@mail.ru")).thenReturn(false);
        when(passwordEncoder.encode("12345678")).thenReturn("hashed_password");
        when(userRepository.save(any(User.class))).thenAnswer(i -> {
            User us = i.getArgument(0);
            us.setId("user-id-1");
            return us;
        });
        when(jwtService.generateAccessToken(anyString(), any(Role.class))).thenReturn("access_token");
        when(jwtService.generateRefreshToken(anyString())).thenReturn("refresh_token");
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(new RefreshToken());

        //when

        AuthResponse response = authService.register(request);

        // then
        assertThat(response.getAccessToken()).isEqualTo("access_token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh_token");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Регистрация - существующий пользователь")
    public void register_fail(){

        // given

        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@mail.ru");
        request.setPassword("12345678");

        when(userRepository.existsByEmail("test@mail.ru")).thenReturn(true);

        // when / then

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(AuthException.class)
                .hasMessageContaining("уже существует");

        verify(userRepository, never()).save(any(User.class));
        verify(refreshTokenRepository, never()).save(any(RefreshToken.class));
    }

    /* ТЕСТЫ С ЛОГИНОМ */

    @Test
    @DisplayName("Логин - успешный вход")
    public void login_success(){

        // given

        LoginRequest request = new LoginRequest();
        request.setEmail("test@mail.ru");
        request.setPassword("12345678");

        User user = User.builder()
                .id("user-id-1")
                .email("test@mail.ru")
                .password("hashed_password")
                .role(Role.CLIENT)
                .build();

        when(userRepository.findByEmail("test@mail.ru")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("12345678", "hashed_password")).thenReturn(true);
        when(jwtService.generateAccessToken(anyString(), any(Role.class))).thenReturn("access_token");
        when(jwtService.generateRefreshToken(anyString())).thenReturn("refresh_token");
        //when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(new RefreshToken());

        // when
        AuthResponse response = authService.login(request);

        // then
        assertThat(response.getAccessToken()).isEqualTo("access_token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh_token");
    }

    @Test
    @DisplayName("Логин - пользователь не найден")
    public void login_fail(){

        // given

        LoginRequest request = new LoginRequest();
        request.setEmail("test@mail.ru");
        request.setPassword("12345678");

        when(userRepository.findByEmail("test@mail.ru")).thenReturn(Optional.empty());

        // when / then

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(AuthException.class)
                .hasMessageContaining("Пользователь не найден !");

    }

    @Test
    @DisplayName("Логин - неверный пароль")
    public void login_fail_wrong_password(){
        LoginRequest request = new LoginRequest();
        request.setEmail("test@mail.ru");
        request.setPassword("12345678");

        User user = User.builder()
                .id("user-id-1")
                .email("test@mail.ru")
                .password("hashed_password")
                .role(Role.CLIENT)
                .build();

        when(userRepository.findByEmail("test@mail.ru")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("12345678", "hashed_password")).thenReturn(false);

        // when / then
        assertThatThrownBy(() -> authService.login(request))
            .isInstanceOf(AuthException.class)
                .hasMessageContaining("Неверный email или password !");
    }

    /* ТЕСТЫ С ЛОГАУТОМ */

    @Test
    @DisplayName("Логаут - удаляет refresh-токены пользователя")
    public void logout_success(){

        //when

        authService.logout("user-id-1");

        // then

        verify(refreshTokenRepository).deleteByUserId("user-id-1");
    }

}
