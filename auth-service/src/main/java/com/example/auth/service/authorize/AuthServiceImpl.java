package com.example.auth.service.authorize;

import com.example.auth.dto.request.LoginRequest;
import com.example.auth.dto.request.RefreshRequest;
import com.example.auth.dto.request.RegisterRequest;
import com.example.auth.dto.response.AuthResponse;
import com.example.auth.exception.AuthException;
import com.example.auth.model.RefreshToken;
import com.example.auth.model.Role;
import com.example.auth.model.User;
import com.example.auth.repository.RefreshTokenRepository;
import com.example.auth.repository.UserRepository;
import com.example.auth.service.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) throws AuthException {
        if(userRepository.findByEmail(request.getEmail()).isPresent()){
            throw new AuthException("Пользователь уже существует !");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.CLIENT)
                .build();

        userRepository.save(user);
        log.info("Новый пользователь: {} ", user.getEmail());

        return buildAuthResponse(user);
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) throws AuthException {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow( () ->
                new AuthException("Пользователь не найден !"));

        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new AuthException("Неверный email или password !");
        }

        log.info("Вход в систему: {} ", user.getEmail());

        return buildAuthResponse(user);
    }

    @Override
    public AuthResponse refresh(RefreshRequest request) throws AuthException {
        String refreshToken = request.getRefreshToken();

        if(!jwtService.isTokenValid(refreshToken)){
            throw new AuthException("Токен не действителен или истёк !");
        }

        RefreshToken saved = refreshTokenRepository.findByToken(refreshToken).orElseThrow(() ->
                new AuthException("Refresh токен не найден !"));

        User user = userRepository.findById(saved.getUserId()).orElseThrow(() ->
                new AuthException("Пользователь не найден !"));

        refreshTokenRepository.deleteByToken(refreshToken);
        return buildAuthResponse(user);
    }


    @Override
    public void logout(String userId) {
        refreshTokenRepository.deleteByUserId(userId);
        log.info("Выход из системы: {} ", userId);
    }

    private AuthResponse buildAuthResponse(User user){
        String accessToken = jwtService.generateAccessToken(user.getId(), user.getRole());
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());

        return new AuthResponse(accessToken, refreshToken);
    }
}
