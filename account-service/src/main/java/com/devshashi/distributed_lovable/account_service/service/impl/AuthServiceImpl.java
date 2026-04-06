package com.devshashi.distributed_lovable.account_service.service.impl;

import com.devshashi.distributed_lovable.account_service.dto.auth.AuthResponse;
import com.devshashi.distributed_lovable.account_service.dto.auth.LoginRequest;
import com.devshashi.distributed_lovable.account_service.dto.auth.SignupRequest;
import com.devshashi.distributed_lovable.account_service.entity.User;
import com.devshashi.distributed_lovable.account_service.mapper.UserMapper;
import com.devshashi.distributed_lovable.account_service.repository.UserRepository;
import com.devshashi.distributed_lovable.account_service.service.AuthService;
import com.devshashi.distributed_lovable.common_lib.error.BadRequestException;
import com.devshashi.distributed_lovable.common_lib.security.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthUtil authUtil;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthResponse signup(SignupRequest request) {
        userRepository.findByUsername(request.username()).ifPresent(user -> {
            throw new BadRequestException("User already exists with username " + request.username());
        });
        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.password()));
        user = userRepository.save(user);
        String token = authUtil.generateAccessToken(userMapper.toUserDTO(user));
        return new AuthResponse(token, userMapper.toUserProfileResponse(user));
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );
        User user = (User) authentication.getPrincipal();
        String token = authUtil.generateAccessToken(userMapper.toUserDTO(user));
        return new AuthResponse(token, userMapper.toUserProfileResponse(user));
    }
}
