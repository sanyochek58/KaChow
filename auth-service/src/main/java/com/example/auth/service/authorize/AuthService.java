package com.example.auth.service.authorize;

import com.example.auth.dto.request.LoginRequest;
import com.example.auth.dto.request.RefreshRequest;
import com.example.auth.dto.request.RegisterRequest;
import com.example.auth.dto.response.AuthResponse;
import com.example.auth.exception.AuthException;

public interface AuthService {
    AuthResponse login(LoginRequest loginRequest) throws AuthException;
    AuthResponse register(RegisterRequest registerRequest) throws AuthException;
    AuthResponse refresh(RefreshRequest refreshRequest) throws AuthException;
    void logout(String userId);
}
