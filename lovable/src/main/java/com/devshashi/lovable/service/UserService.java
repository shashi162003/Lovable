package com.devshashi.lovable.service;

import com.devshashi.lovable.dto.auth.UserProfileResponse;

public interface UserService {
    UserProfileResponse getProfile(Long userId);
}
