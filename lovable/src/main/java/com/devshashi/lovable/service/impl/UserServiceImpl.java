package com.devshashi.lovable.service.impl;

import com.devshashi.lovable.dto.auth.UserProfileResponse;
import com.devshashi.lovable.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Override
    public UserProfileResponse getProfile(Long userId) {
        return null;
    }
}
