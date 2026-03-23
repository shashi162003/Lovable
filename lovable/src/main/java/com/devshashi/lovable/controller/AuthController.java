package com.devshashi.lovable.controller;

import com.devshashi.lovable.dto.auth.AuthResponse;
import com.devshashi.lovable.dto.auth.LoginRequest;
import com.devshashi.lovable.dto.auth.SignupRequest;
import com.devshashi.lovable.dto.auth.UserProfileResponse;
import com.devshashi.lovable.service.AuthService;
import com.devshashi.lovable.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@RequestBody SignupRequest request){
        return ResponseEntity.ok(authService.signup(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request){
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getProfile(){
        Long userId = 1L;
        return ResponseEntity.ok(userService.getProfile(userId));
    }

}
