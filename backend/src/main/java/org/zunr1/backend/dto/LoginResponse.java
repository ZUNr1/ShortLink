package org.zunr1.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private Long userId;
    private String name;
    private String email;
    private String accessToken;   // 访问令牌
    private String refreshToken;  // 刷新令牌
    private Long expiresIn;       // access_token 过期时间（秒）
}
