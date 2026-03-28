package com.example.auth.controller;

import com.example.auth.config.SecurityConfig;
import com.example.auth.dto.request.LoginRequest;
import com.example.auth.dto.request.RefreshRequest;
import com.example.auth.dto.request.RegisterRequest;
import com.example.auth.dto.response.AuthResponse;
import com.example.auth.exception.AuthException;
import com.example.auth.exception.GlobalExceptionHandler;
import com.example.auth.service.authorize.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import({GlobalExceptionHandler.class, SecurityConfig.class})
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    /* REGISTER */

    @Test
    @DisplayName("POST /api/auth/register - 200 при успехе")
    public void register_success() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@mail.ru");
        request.setPassword("12345678");

        AuthResponse authResponse = AuthResponse.builder()
                .accessToken("access-token")
                .refreshToken("refresh-token")
                .build();

        when(authService.register(any())).thenReturn(authResponse);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"));
    }

    @Test
    @DisplayName("POST /api/auth/register - 400 при невалидном email")
    public void register_invalidEmail() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("not-an-email");
        request.setPassword("12345678");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/auth/register - 400 при коротком пароле")
    public void register_shortPassword() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@mail.ru");
        request.setPassword("123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/auth/register - 401 если email занят")
    public void register_emailTaken() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@mail.ru");
        request.setPassword("12345678");

        when(authService.register(any()))
                .thenThrow(new AuthException("Пользователь уже существует !"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Пользователь уже существует !"));
    }

    /* LOGIN */

    @Test
    @DisplayName("POST /api/auth/login - 200 при успехе")
    public void login_success() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@mail.ru");
        request.setPassword("12345678");

        AuthResponse authResponse = AuthResponse.builder()
                .accessToken("access-token")
                .refreshToken("refresh-token")
                .build();

        when(authService.login(any())).thenReturn(authResponse);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"));
    }

    @Test
    @DisplayName("POST /api/auth/login - 401 при неверных данных")
    public void login_fail() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@mail.ru");
        request.setPassword("12345678");

        when(authService.login(any()))
                .thenThrow(new AuthException("Неверный email или password !"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Неверный email или password !"));
    }

    /* REFRESH */

    @Test
    @DisplayName("POST /api/auth/refresh - 200 при успехе")
    public void refresh_success() throws Exception {
        RefreshRequest request = new RefreshRequest();
        request.setRefreshToken("valid-refresh-token");

        AuthResponse authResponse = AuthResponse.builder()
                .accessToken("new-access-token")
                .refreshToken("new-refresh-token")
                .build();

        when(authService.refresh(any())).thenReturn(authResponse);

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("new-access-token"))
                .andExpect(jsonPath("$.refreshToken").value("new-refresh-token"));
    }

    @Test
    @DisplayName("POST /api/auth/refresh - 401 если токен невалиден")
    public void refresh_invalidToken() throws Exception {
        RefreshRequest request = new RefreshRequest();
        request.setRefreshToken("expired-token");

        when(authService.refresh(any()))
                .thenThrow(new AuthException("Токен не действителен или истёк !"));

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Токен не действителен или истёк !"));
    }

    /* LOGOUT */

    @Test
    @DisplayName("POST /auth/logout - 200 при успехе")
    public void logout_success() throws Exception {
        doNothing().when(authService).logout(any());

        mockMvc.perform(post("/api/auth/logout")
                        .header("X-User-Id", "user-id-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Успешный выход !"));
    }
}