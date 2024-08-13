
package com.soongsil.CoffeeChat.config.oauth2;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.soongsil.CoffeeChat.config.jwt.JWTUtil;
import com.soongsil.CoffeeChat.dto.Oauth.CustomOAuth2User;
import com.soongsil.CoffeeChat.entity.Refresh;
import com.soongsil.CoffeeChat.repository.RefreshRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

//로그인이 성공했을 때 받은 데이터들을 바탕으로 JWT발급을 위한 핸들러
@Component
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
	private final JWTUtil jwtUtil;
	private final RefreshRepository refreshRepository;

	public CustomSuccessHandler(JWTUtil jwtUtil, RefreshRepository refreshRepository) {
		this.jwtUtil = jwtUtil;
		this.refreshRepository = refreshRepository;
	}

	private void addRefreshEntity(String username, String refresh, Long expiredMs) {
		Date date = new Date(System.currentTimeMillis() + expiredMs);

		Refresh refreshEntity = new Refresh();
		refreshEntity.setUsername(username);
		refreshEntity.setRefresh(refresh);
		refreshEntity.setExpiration(date.toString());

		refreshRepository.save(refreshEntity);
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException, ServletException {

		CustomOAuth2User customUserDetails = (CustomOAuth2User)authentication.getPrincipal();

		String username = customUserDetails.getUsername();

		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
		GrantedAuthority auth = iterator.next();
		String role = auth.getAuthority();

		//String accessToken = jwtUtil.createJwt("access", username, role, 600000L);  // 10분
		String accessToken = jwtUtil.createJwt("access", username, role, 180000L);  // 10분
		System.out.println("accessToken = " + accessToken);
		String refreshToken = jwtUtil.createJwt("refresh", username, role, 86400000L); // 24시간

		addRefreshEntity(username, refreshToken, 86400000L);

		// Refresh 토큰 쿠키에 추가
		addSameSiteCookie(response, "refresh", refreshToken);

		// loginStatus 쿠키 추가
		if (role.equals("ROLE_USER")) {
			addSameSiteCookie(response, "loginStatus", "signup");
		} else if (role.equals("ROLE_MENTEE") || role.equals("ROLE_MENTOR")) {
			addSameSiteCookie(response, "loginStatus", "main");
		}

		response.setStatus(HttpStatus.OK.value());
		//response.sendRedirect("http://localhost:8080/swagger-ui/index.html"); //서버 로컬 테스트용
		response.sendRedirect("https://localhost:3000/callback");
		//response.sendRedirect("https://coffeego-ssu.web.app/callback");
	}

	private void addSameSiteCookie(HttpServletResponse response, String name, String value) {
		ResponseCookie responseCookie = ResponseCookie.from(name, value)
			.httpOnly(true)
			.secure(true)
			.path("/")
			.maxAge(24 * 60 * 60)
			.sameSite("None")
			.build();

		response.addHeader("Set-Cookie", responseCookie.toString());
	}
}

/*
package com.soongsil.CoffeeChat.config.oauth2;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.soongsil.CoffeeChat.config.jwt.JWTUtil;
import com.soongsil.CoffeeChat.dto.Oauth.CustomOAuth2User;
import com.soongsil.CoffeeChat.repository.RefreshRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
	private final JWTUtil jwtUtil;
	private final RedisTemplate<String, String> redisTemplate;

	public CustomSuccessHandler(JWTUtil jwtUtil, RedisTemplate<String, String> redisTemplate) {
		this.jwtUtil = jwtUtil;
		this.redisTemplate = redisTemplate;
	}

	private void addRefreshToken(String username, String refresh, Long expiredMs) {
		String key = "refresh_token:" + username;
		redisTemplate.opsForValue().set(key, refresh, Duration.ofMillis(expiredMs));
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
										Authentication authentication) throws IOException, ServletException {

		CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();

		String username = customUserDetails.getUsername();

		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
		GrantedAuthority auth = iterator.next();
		String role = auth.getAuthority();

		String accessToken = jwtUtil.createJwt("access", username, role, 180000L);  // 10분
		System.out.println("accessToken = " + accessToken);
		String refreshToken = jwtUtil.createJwt("refresh", username, role, 86400000L); // 24시간

		// Refresh Token을 Redis에 저장
		addRefreshToken(username, refreshToken, 86400000L);

		// Refresh 토큰 쿠키에 추가
		addSameSiteCookie(response, "refresh", refreshToken);

		// loginStatus 쿠키 추가
		if (role.equals("ROLE_USER")) {
			addSameSiteCookie(response, "loginStatus", "signup");
		} else if (role.equals("ROLE_MENTEE") || role.equals("ROLE_MENTOR")) {
			addSameSiteCookie(response, "loginStatus", "main");
		}

		response.setStatus(HttpStatus.OK.value());
		response.sendRedirect("https://localhost:3000/callback");
	}

	private void addSameSiteCookie(HttpServletResponse response, String name, String value) {
		ResponseCookie responseCookie = ResponseCookie.from(name, value)
				.httpOnly(true)
				.secure(true)
				.path("/")
				.maxAge(24 * 60 * 60)
				.sameSite("None")
				.build();

		response.addHeader("Set-Cookie", responseCookie.toString());
	}
}

 */



