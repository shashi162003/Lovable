package com.devshashi.lovable.dto.auth;

public record LoginRequest(
        String email,
        String password
) {
}
