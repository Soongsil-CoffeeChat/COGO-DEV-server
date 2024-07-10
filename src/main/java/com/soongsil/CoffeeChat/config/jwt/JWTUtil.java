package com.soongsil.CoffeeChat.config.jwt;

import java.nio.charset.StandardCharsets;
import java.security.SignatureException;
import java.util.Date;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

//JWT : username, role, 생성일, 만료일 포함, 0.12.3 버전 사용
//username확인, role확인, 만료일 확인
@Component
public class JWTUtil {
	private SecretKey secretKey;

	public JWTUtil(@Value("${spring.jwt.secret}") String secret) {
		secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),
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

	public boolean isExpired(String token) {
		try {
			// JWT 파서 생성 및 비밀키 설정
			var jwtParser = Jwts.parser().verifyWith(secretKey).build();

			// 토큰 파싱 및 클레임 추출
			var claims = jwtParser.parseSignedClaims(token).getPayload();

			// 만료 시간 추출
			var expiration = claims.getExpiration();

			// 만료 여부 확인
			return !expiration.before(new Date());
		} catch (ExpiredJwtException e) {
			System.out.println("Token is expired: " + e.getMessage());
			return true;
		} catch (MalformedJwtException e) {
			System.out.println("Malformed token: " + e.getMessage());
			return false;
		} catch (UnsupportedJwtException e) {
			System.out.println("Unsupported token: " + e.getMessage());
			return false;
		} catch (IllegalArgumentException e) {
			System.out.println("Illegal argument token: " + e.getMessage());
			return false;
		} catch (Exception e) {
			System.out.println("Invalid token: " + e.getMessage());
			return false;
		}
	}



	public String getCategory(String token) {  //토큰의 카테고리 꺼내는 로직 추가
		return Jwts.parser()
			.verifyWith(secretKey)
			.build()
			.parseSignedClaims(token)
			.getPayload()
			.get("category", String.class);
	}

	public String createJwt(String category, String username, String role, Long expiredMs) { //토큰생성
		return Jwts.builder()
			.claim("category", category)
			.claim("username", username)
			.claim("role", role)
			.issuedAt(new Date(System.currentTimeMillis()))
			.expiration(new Date(System.currentTimeMillis() + expiredMs))
			.signWith(secretKey)
			.compact();
	}
}
