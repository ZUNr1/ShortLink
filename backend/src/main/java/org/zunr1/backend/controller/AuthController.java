package org.zunr1.backend.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zunr1.backend.dto.LoginRequest;
import org.zunr1.backend.dto.LoginResponse;
import org.zunr1.backend.dto.RegisterRequest;
import org.zunr1.backend.dto.Result;
import org.zunr1.backend.service.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Result<LoginResponse>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        LoginResponse response = authService.register(registerRequest);
        return ResponseEntity.ok(Result.success(response));
    }

    @PostMapping("/login")
    public ResponseEntity<Result<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse response = authService.login(loginRequest);
        return ResponseEntity.ok(Result.success(response));
    }

    @PostMapping("/refresh")
    public ResponseEntity<Result<LoginResponse>> refresh(@Valid @RequestParam String refreshToken) {
        LoginResponse response = authService.refreshAccessToken(refreshToken);
        return ResponseEntity.ok(Result.success(response));
    }
}
