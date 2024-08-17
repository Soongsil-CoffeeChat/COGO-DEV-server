
package com.soongsil.CoffeeChat.service;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.soongsil.CoffeeChat.config.jwt.JWTUtil;
import com.soongsil.CoffeeChat.entity.Refresh;
import com.soongsil.CoffeeChat.repository.RefreshRepository;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class RefreshTokenService {
	private final JWTUtil jwtUtil;
	private final RefreshRepository refreshRepository;

	public RefreshTokenService(JWTUtil jwtUtil, RefreshRepository refreshRepository) {
		this.jwtUtil = jwtUtil;
		this.refreshRepository = refreshRepository;
	}


	private void addRefreshEntity(String username, String refresh, Long expiredMs) {  //Refresh객체를 DB에 저장(블랙리스트관리)

		Date date = new Date(System.currentTimeMillis() + expiredMs);

		Refresh refreshEntity = new Refresh();
		refreshEntity.setUsername(username);
		refreshEntity.setRefresh(refresh);
		refreshEntity.setExpiration(date.toString());

		refreshRepository.save(refreshEntity);
	}

	public ResponseEntity<?> reissueByRefreshToken(HttpServletRequest request, HttpServletResponse response) {
		// Get refresh token
		String refresh = null;
		String loginStatus = null;
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("loginStatus")) {
					loginStatus = cookie.getValue();
				}
				if (cookie.getName().equals("refresh")) {
					refresh = cookie.getValue();
					System.out.println("refresh = " + refresh);
					System.out.println("리프레쉬토큰 찾음");
				}
			}
		}
		System.out.println("refresh = " + refresh);
		if (refresh == null) {
			// Response status code 400 (refresh 토큰이 들어오지 않음)
			return new ResponseEntity<>("refresh token null", HttpStatus.BAD_REQUEST);
		}

		// Expired check
		try {
			jwtUtil.isExpired(refresh);
		} catch (ExpiredJwtException e) {
			// Response status code 400 (refresh 토큰이 만료됨)
			return new ResponseEntity<>("refresh token expired", HttpStatus.BAD_REQUEST);
		}

		// 토큰이 refresh인지 확인 (발급 시 페이로드에 명시)
		String category = jwtUtil.getCategory(refresh);

		if (!category.equals("refresh")) {
			// Response status code 400 (들어온 토큰이 refresh 토큰이 아님)
			return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
		}

		// DB에 저장되어 있는지 확인
		Boolean isExist = refreshRepository.existsByRefresh(refresh);
		if (!isExist) {
			// Response status code 400 (들어온 refresh 토큰이 내 DB에 저장된 목록에 없음)
			return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
		}

		String username = jwtUtil.getUsername(refresh);
		String role = jwtUtil.getRole(refresh);

		// Make new JWT
		String newAccess = jwtUtil.createJwt("access", username, role, Long.MAX_VALUE);
		String newRefresh = jwtUtil.createJwt("refresh", username, role, Long.MAX_VALUE);

		// Refresh 토큰 저장: DB에 기존의 Refresh 토큰 삭제 후 새 Refresh 토큰 저장
		refreshRepository.deleteByRefresh(refresh);
		addRefreshEntity(username, newRefresh, 86400000L);

		// Response
		response.setHeader("access", newAccess);
		response.setHeader("loginStatus", loginStatus);

		// SameSite 설정을 포함한 쿠키 추가
		ResponseCookie responseCookie = ResponseCookie.from("refresh", newRefresh)
				.httpOnly(true)
				.secure(true)
				.path("/")
				.maxAge(24 * 60 * 60)
				.sameSite("None")
				.build();

		response.addHeader("Set-Cookie", responseCookie.toString());

		return new ResponseEntity<>(HttpStatus.OK);
	}
}

/*

package com.soongsil.CoffeeChat.service;

import java.time.Duration;
import java.util.Date;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.soongsil.CoffeeChat.config.jwt.JWTUtil;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class RefreshTokenService {
	private final JWTUtil jwtUtil;
	private final RedisTemplate<String, String> redisTemplate;
	private static final long REFRESH_TOKEN_TTL = 86400000L; // 24 hours in milliseconds

	public RefreshTokenService(JWTUtil jwtUtil, RedisTemplate<String, String> redisTemplate) {
		this.jwtUtil = jwtUtil;
		this.redisTemplate = redisTemplate;
	}

	private void addRefreshToken(String username, String refresh, Long expiredMs) {
		String key = "refresh_token:" + username;
		redisTemplate.opsForValue().set(key, refresh, Duration.ofMillis(expiredMs));
	}

	public ResponseEntity<?> reissueByRefreshToken(HttpServletRequest request, HttpServletResponse response) {
		// Get refresh token
		String refresh = null;
		String loginStatus = null;
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("loginStatus")) {
					loginStatus = cookie.getValue();
				}
				if (cookie.getName().equals("refresh")) {
					refresh = cookie.getValue();
					System.out.println("refresh = " + refresh);
					System.out.println("리프레쉬토큰 찾음");
				}
			}
		}
		System.out.println("refresh = " + refresh);
		if (refresh == null) {
			// Response status code 400 (refresh 토큰이 들어오지 않음)
			return new ResponseEntity<>("refresh token null", HttpStatus.BAD_REQUEST);
		}

		// Expired check
		try {
			jwtUtil.isExpired(refresh);
		} catch (ExpiredJwtException e) {
			// Response status code 400 (refresh 토큰이 만료됨)
			return new ResponseEntity<>("refresh token expired", HttpStatus.BAD_REQUEST);
		}

		// 토큰이 refresh인지 확인 (발급 시 페이로드에 명시)
		String category = jwtUtil.getCategory(refresh);

		if (!category.equals("refresh")) {
			// Response status code 400 (들어온 토큰이 refresh 토큰이 아님)
			return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
		}

		// Redis에 저장되어 있는지 확인
		String username = jwtUtil.getUsername(refresh);
		String storedRefreshToken = redisTemplate.opsForValue().get("refresh_token:" + username);
		if (storedRefreshToken == null || !storedRefreshToken.equals(refresh)) {
			// Response status code 400 (들어온 refresh 토큰이 Redis에 저장된 목록에 없음)
			return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
		}

		String role = jwtUtil.getRole(refresh);

		// Make new JWT
		String newAccess = jwtUtil.createJwt("access", username, role, 180000L);
		String newRefresh = jwtUtil.createJwt("refresh", username, role, REFRESH_TOKEN_TTL);

		// Redis에 새 Refresh 토큰 저장
		addRefreshToken(username, newRefresh, REFRESH_TOKEN_TTL);

		// Response
		response.setHeader("access", newAccess);
		response.setHeader("loginStatus", loginStatus);

		// SameSite 설정을 포함한 쿠키 추가
		ResponseCookie responseCookie = ResponseCookie.from("refresh", newRefresh)
				.httpOnly(true)
				.secure(true)
				.path("/")
				.maxAge(24 * 60 * 60)
				.sameSite("None")
				.build();

		response.addHeader("Set-Cookie", responseCookie.toString());

		return new ResponseEntity<>(HttpStatus.OK);
	}
}

 */


