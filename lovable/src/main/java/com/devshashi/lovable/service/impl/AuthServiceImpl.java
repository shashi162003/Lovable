package com.devshashi.lovable.service.impl;

import com.devshashi.lovable.dto.auth.AuthResponse;
import com.devshashi.lovable.dto.auth.LoginRequest;
import com.devshashi.lovable.dto.auth.SignupRequest;
import com.devshashi.lovable.service.AuthService;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Override
    public AuthResponse signup(SignupRequest request) {
        return null;
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        return null;
    }
}
