package com.devshashi.distributed_lovable.account_service.controller;

import com.devshashi.distributed_lovable.account_service.dto.auth.AuthResponse;
import com.devshashi.distributed_lovable.account_service.dto.auth.LoginRequest;
import com.devshashi.distributed_lovable.account_service.dto.auth.SignupRequest;
import com.devshashi.distributed_lovable.account_service.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@RequestBody SignupRequest request){
        return ResponseEntity.ok(authService.signup(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request){
        return ResponseEntity.ok(authService.login(request));
    }
}
