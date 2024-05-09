package com.soongsil.CoffeeChat.service;

import java.util.Date;

import org.springframework.http.HttpStatus;
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

	private Cookie createCookie(String key, String value) {

		Cookie cookie = new Cookie(key, value);
		cookie.setMaxAge(24 * 60 * 60);
		//cookie.setSecure(true);
		//cookie.setPath("/");
		cookie.setHttpOnly(true);

		return cookie;
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
		//get refresh token
		String refresh = null;
		Cookie[] cookies = request.getCookies();
		for (Cookie cookie : cookies) {

			if (cookie.getName().equals("refresh")) {

				refresh = cookie.getValue();
			}
		}

		if (refresh == null) {

			//response status code 400 (refresh토큰이 들어오지 않음)
			return new ResponseEntity<>("refresh token null", HttpStatus.BAD_REQUEST);
		}

		//expired check
		JWTUtil jwtUtil = null;
		try {
			jwtUtil.isExpired(refresh);
		} catch (ExpiredJwtException e) {

			//response status code 400 (refresh 토큰이 만료됨)
			return new ResponseEntity<>("refresh token expired", HttpStatus.BAD_REQUEST);
		}

		// 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
		String category = jwtUtil.getCategory(refresh);

		if (!category.equals("refresh")) {

			//response status code 400 (들어온 토큰이 refresh토큰이 아님)
			return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
		}

		//DB에 저장되어 있는지 확인
		Boolean isExist = refreshRepository.existsByRefresh(refresh);
		if (!isExist) {

			//response body status code 400 (들어올 refresh토큰이 내 DB에 저장된 목록에 없음)
			return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
		}

		String username = jwtUtil.getUsername(refresh);
		String role = jwtUtil.getRole(refresh);

		//make new JWT
		String newAccess = jwtUtil.createJwt("access", username, role, 600000L);
		String newRefresh = jwtUtil.createJwt("refresh", username, role, 86400000L);

		//Refresh 토큰 저장 DB에 기존의 Refresh 토큰 삭제 후 새 Refresh 토큰 저장
		refreshRepository.deleteByRefresh(refresh);
		addRefreshEntity(username, newRefresh, 86400000L);

		//response
		response.setHeader("access", newAccess);
		response.addCookie(createCookie("refresh", newRefresh));

		return new ResponseEntity<>(HttpStatus.OK);

	}
}
