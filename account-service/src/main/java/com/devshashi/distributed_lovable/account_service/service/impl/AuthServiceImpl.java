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
import com.devshashi.distributed_lovable.common_lib.security.JwtUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

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

        JwtUserPrincipal jwtUserPrincipal = new JwtUserPrincipal(user.getId(), user.getName(), user.getUsername(), null, new ArrayList<>());

        String token = authUtil.generateAccessToken(jwtUserPrincipal);
        return new AuthResponse(token, userMapper.toUserProfileResponse(jwtUserPrincipal));
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        JwtUserPrincipal user = (JwtUserPrincipal) authentication.getPrincipal();
        String token = authUtil.generateAccessToken(user);

        return new AuthResponse(token, userMapper.toUserProfileResponse(user));
    }
}
