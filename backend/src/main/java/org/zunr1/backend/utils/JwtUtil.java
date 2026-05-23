package org.zunr1.backend.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;  // 访问令牌过期时间

    @Value("${jwt.refresh-expiration}")
    private Long refreshExpiration;  // 刷新令牌过期时间

    /**
     * 生成访问令牌
     */
    public String generateAccessToken(Long id) {
        return JWT.create()
                .withClaim("id", id)
                .withClaim("type", "access")
                .withExpiresAt(new Date(System.currentTimeMillis() + expiration))
                .sign(Algorithm.HMAC256(secret));
    }

    /**
     * 生成刷新令牌
     */
    public String generateRefreshToken(Long id) {
        return JWT.create()
                .withClaim("id", id)
                .withClaim("type", "refresh")
                .withExpiresAt(new Date(System.currentTimeMillis() + refreshExpiration))
                .sign(Algorithm.HMAC256(secret));
    }

    /**
     * 验证令牌并返回用户 ID
     *
     * @return 验证成功返回用户ID，失败返回null
     */
    public Long verify(String token) {
        try {
            DecodedJWT jwt = JWT.require(Algorithm.HMAC256(secret))
                    .build()
                    .verify(token);
            return jwt.getClaim("id").asLong();
        } catch (TokenExpiredException e) {
            log.warn("Token已过期: {}", e.getMessage());
            return null;
        } catch (JWTVerificationException e) {
            log.warn("Token验证失败: {}", e.getMessage());
            return null;
        } catch (Exception e) {
            log.error("Token验证异常: ", e);
            return null;
        }
    }
}