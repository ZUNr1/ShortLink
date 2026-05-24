package org.zunr1.backend.service;

import org.zunr1.backend.dto.LoginRequest;
import org.zunr1.backend.dto.LoginResponse;
import org.zunr1.backend.dto.RegisterRequest;

public interface AuthService {

    LoginResponse register(RegisterRequest registerRequest);
    LoginResponse login(LoginRequest loginRequest);
    LoginResponse refreshAccessToken(String refreshToken);
    boolean checkUser(Long userId);

}
