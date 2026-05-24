package org.zunr1.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.zunr1.backend.dto.LoginRequest;
import org.zunr1.backend.dto.LoginResponse;
import org.zunr1.backend.dto.RegisterRequest;
import org.zunr1.backend.entity.User;
import org.zunr1.backend.exception.BadRequestException;
import org.zunr1.backend.exception.TokenErrorException;
import org.zunr1.backend.mapper.UserMapper;
import org.zunr1.backend.utils.JwtUtil;

@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;  // 毫秒

    @Override
    public LoginResponse register(RegisterRequest registerRequest) {
        User existingUser = userMapper.selectByEmail(registerRequest.getEmail());
        if (existingUser != null) {
            throw new BadRequestException("邮箱已被注册");
        }
        existingUser = userMapper.selectByName(registerRequest.getName());
        if (existingUser != null) {
            throw new BadRequestException("用户名已被注册");
        }
        User user = new User();
        user.setName(registerRequest.getName());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(0);
        user.setStatus(1);
        int rows = userMapper.insertUser(user);
        if (rows <= 0){
            throw new RuntimeException("注册失败");
        }
        return generateLoginResponse(user);
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        User user = userMapper.selectByEmail(loginRequest.getEmail());
        if (user == null) {
            throw new BadRequestException("邮箱或密码错误");
        }
        if (user.getStatus() == null || user.getStatus() == 0) {
            throw new BadRequestException("用户已被禁用");
        }
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new BadRequestException("邮箱或密码错误");
        }
        return generateLoginResponse(user);
    }

    @Override
    public LoginResponse refreshAccessToken(String refreshToken) {
        Long userId = jwtUtil.verify(refreshToken);
        if (userId == null) {
            throw new TokenErrorException("无效的刷新令牌");
        }
        if (!checkUser(userId)) {
            throw new TokenErrorException("用户不存在或已注销");
        }
        User user = userMapper.selectById(userId);
        String newAccessToken = jwtUtil.generateAccessToken(userId);
        return LoginResponse.builder()
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .expiresIn(jwtExpiration / 1000)
                .build();
    }

    @Override
    public boolean checkUser(Long userId) {
        if (userId == null) {
            return false;
        }
        User user = userMapper.selectById(userId);
        return user != null && user.getStatus() != null && user.getStatus() == 1;
    }

    private LoginResponse generateLoginResponse(User user) {
        String accessToken = jwtUtil.generateAccessToken(user.getId());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId());
        return LoginResponse.builder()
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(jwtExpiration / 1000)
                .build();
    }
}
