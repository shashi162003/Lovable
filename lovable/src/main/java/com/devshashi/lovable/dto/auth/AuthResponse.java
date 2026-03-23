package com.devshashi.lovable.dto.auth;

public record AuthResponse(
        String token,
        UserProfileResponse userProfileResponse
) {

}
