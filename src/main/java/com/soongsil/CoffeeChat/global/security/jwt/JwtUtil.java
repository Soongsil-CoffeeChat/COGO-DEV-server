package com.soongsil.CoffeeChat.global.security.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import com.soongsil.CoffeeChat.global.exception.GlobalErrorCode;
import com.soongsil.CoffeeChat.global.exception.GlobalException;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;

// JWT : username, role, 생성일, 만료일 포함, 0.12.3 버전 사용
// username확인, role확인, 만료일 확인
@Component
public class JwtUtil {
    private final SecretKey secretKey;

    public JwtUtil(@Value("${spring.jwt.secret}") String secret) {
        secretKey =
                new SecretKeySpec(
                        secret.getBytes(StandardCharsets.UTF_8),
                        Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public String getUsername(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("username", String.class);
    }

    public String getRole(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("role", String.class);
    }

    // fun validateToken(token: String): Boolean {
    //        try {
    //            return Jwts.parserBuilder().setSigningKey(this.key).build()
    //                .parseClaimsJws(token).body.expiration.after(Date())
    //        } catch (e: SignatureException) {
    //            throw GlobalException(GlobalErrorCode.JWT_INVALID_TOKEN)
    //        } catch (e: MalformedJwtException) {
    //            throw GlobalException(GlobalErrorCode.JWT_MALFORMED_TOKEN)
    //        } catch (e: ExpiredJwtException) {
    //            throw GlobalException(GlobalErrorCode.JWT_EXPIRED_TOKEN)
    //        } catch (e: UnsupportedJwtException) {
    //            throw GlobalException(GlobalErrorCode.JWT_UNSUPPORTED_TOKEN)
    //        } catch (e: IllegalArgumentException) {
    //            throw GlobalException(GlobalErrorCode.INTERNAL_SERVER_ERROR)
    //        }
    //    }
    public boolean validateToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getExpiration()
                    .before(new Date());
        } catch (ExpiredJwtException e) {
            throw new GlobalException(GlobalErrorCode.JWT_EXPIRED_TOKEN);
        } catch (MalformedJwtException e) {
            throw new GlobalException(GlobalErrorCode.JWT_MALFORMED_TOKEN);
        } catch (UnsupportedJwtException e) {
            throw new GlobalException(GlobalErrorCode.JWT_UNSUPPORTED_TOKEN);
        } catch (IllegalArgumentException e) {
            throw new GlobalException(GlobalErrorCode.JWT_INVALID_TOKEN);
        }
    }

    public String getCategory(String token) { // 토큰의 카테고리 꺼내는 로직 추가
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("category", String.class);
    }

    public String createJwt(String category, String username, String role, Long expiredMs) { // 토큰생성
        return Jwts.builder()
                .claim("category", category)
                .claim("username", username)
                .claim("role", role)
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
}
