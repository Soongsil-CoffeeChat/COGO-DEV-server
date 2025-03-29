package com.soongsil.CoffeeChat.global.security.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import com.soongsil.CoffeeChat.domain.entity.enums.Role;
import com.soongsil.CoffeeChat.global.exception.GlobalErrorCode;
import com.soongsil.CoffeeChat.global.exception.GlobalException;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;

// JWT : username, role, 생성일, 만료일 포함, 0.12.3 버전 사용
// username확인, role확인, 만료일 확인
@Component
public class JwtUtil {
    private final SecretKey secretKey;

    // TODO test 이후 시간 줄이기
    private final long accessExpiredMs = 86400000L * 7; // 3600000L; // 1시간
    private final long refreshExpiredMs = 86400000L * 7; // 1주일

    public JwtUtil(@Value("${spring.jwt.secret}") String secret) {
        secretKey =
                new SecretKeySpec(
                        secret.getBytes(StandardCharsets.UTF_8),
                        Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public String getUsername(String token) {
        return getClaims(token).get("username", String.class);
    }

    public Role getRole(String token) {
        return Role.valueOf(getClaims(token).get("role", String.class));
    }

    public void validateToken(String token) {
        try {
            if (getClaims(token).getExpiration().before(new Date()))
                throw new GlobalException(GlobalErrorCode.JWT_EXPIRED_TOKEN);
        } catch (ExpiredJwtException e) {
            throw new GlobalException(GlobalErrorCode.JWT_EXPIRED_TOKEN);
        } catch (MalformedJwtException e) {
            throw new GlobalException(GlobalErrorCode.JWT_MALFORMED_TOKEN);
        } catch (UnsupportedJwtException e) {
            throw new GlobalException(GlobalErrorCode.JWT_UNSUPPORTED_TOKEN);
        } catch (SignatureException | IllegalArgumentException e) {
            throw new GlobalException(GlobalErrorCode.JWT_INVALID_TOKEN);
        }
    }

    public String getCategory(String token) { // 토큰의 카테고리 꺼내는 로직 추가
        return getClaims(token).get("category", String.class);
    }

    public String createAccessToken(String username, Role role) {
        return createJwt("access", username, role, accessExpiredMs);
    }

    public String createRefreshToken(String username, Role role) {
        return createJwt("refresh", username, role, refreshExpiredMs);
    }

    private String createJwt(String category, String username, Role role, Long expiredMs) { // 토큰생성
        return Jwts.builder()
                .claim("category", category)
                .claim("username", username)
                .claim("role", role.name())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }

    public String resolveToken(HttpServletRequest request) {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (token == null || !token.startsWith("Bearer ")) {
            return null;
        }
        return token.substring(7);
    }

    public Claims getClaims(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
    }
}
