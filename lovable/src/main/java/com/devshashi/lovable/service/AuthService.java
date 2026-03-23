package com.devshashi.lovable.service;

import com.devshashi.lovable.dto.auth.AuthResponse;
import com.devshashi.lovable.dto.auth.LoginRequest;
import com.devshashi.lovable.dto.auth.SignupRequest;

public interface AuthService {
    AuthResponse signup(SignupRequest request);

    AuthResponse login(LoginRequest request);
}
