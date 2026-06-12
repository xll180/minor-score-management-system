package com.minor.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SecurityException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 * 用于生成、解析和校验JWT Token
 * 使用HMAC-SHA256算法进行签名
 */
@Component
public class JwtUtil {

    /**
     * JWT密钥（从application.yml注入）
     */
    @Value("${jwt.secret}")
    private String secret;

    /**
     * Token过期时间，单位毫秒（从application.yml注入）
     */
    @Value("${jwt.expiration}")
    private long expiration;

    /**
     * 获取用于HMAC-SHA256签名的密钥对象
     *
     * @return SecretKey实例
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return new SecretKeySpec(keyBytes, "HmacSHA256");
    }

    /**
     * 生成JWT Token
     *
     * @param userId   用户ID
     * @param username 用户名
     * @param role     角色 (ADMIN/TEACHER/STUDENT)
     * @return JWT Token字符串
     */
    public String generateToken(Long userId, String username, String role) {
        // 自定义Claims，存放用户信息
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("role", role);

        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .claims(claims)                      // 设置自定义载荷
                .subject(username)                     // 设置主题（用户名）
                .issuedAt(now)                         // 签发时间
                .expiration(expirationDate)            // 过期时间
                .signWith(getSigningKey())             // 使用HMAC-SHA256签名
                .compact();
    }

    /**
     * 从Token中解析所有Claims
     *
     * @param token JWT Token
     * @return Claims对象
     */
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 从Token中获取用户ID
     *
     * @param token JWT Token
     * @return 用户ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = parseClaims(token);
        return claims.get("userId", Long.class);
    }

    /**
     * 从Token中获取用户名
     *
     * @param token JWT Token
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        Claims claims = parseClaims(token);
        return claims.getSubject();
    }

    /**
     * 从Token中获取角色
     *
     * @param token JWT Token
     * @return 角色字符串
     */
    public String getRoleFromToken(String token) {
        Claims claims = parseClaims(token);
        return claims.get("role", String.class);
    }

    /**
     * 校验Token是否有效
     *
     * @param token JWT Token
     * @return true表示有效，false表示无效
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (SecurityException e) {
            // 签名校验失败
            return false;
        } catch (MalformedJwtException e) {
            // Token格式错误
            return false;
        } catch (ExpiredJwtException e) {
            // Token已过期
            return false;
        } catch (UnsupportedJwtException e) {
            // 不支持的Token
            return false;
        } catch (IllegalArgumentException e) {
            // Token为空
            return false;
        }
    }
}