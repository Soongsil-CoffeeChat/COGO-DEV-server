package com.soongsil.CoffeeChat.config.oauth2;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.soongsil.CoffeeChat.config.jwt.JWTUtil;
import com.soongsil.CoffeeChat.dto.CustomOAuth2User;
import com.soongsil.CoffeeChat.entity.Refresh;
import com.soongsil.CoffeeChat.repository.RefreshRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;


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

		CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();

		String username = customUserDetails.getUsername();

		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
		GrantedAuthority auth = iterator.next();
		String role = auth.getAuthority();

		String accessToken = jwtUtil.createJwt("access", username, role, 600000L);  //10분
		System.out.println("accessToken = " + accessToken);
		String refreshToken = jwtUtil.createJwt("refresh", username, role, 86400000L); //24시간

		addRefreshEntity(username, refreshToken, 86400000L);

		// Refresh 토큰 쿠키에 추가
		addSameSiteCookie(response, createCookie("refresh", refreshToken));

		// loginStatus 쿠키 추가
		if (role.equals("ROLE_USER"))
			addSameSiteCookie(response, createCookie("loginStatus", "signup"));
		else if (role.equals("ROLE_MENTEE") || role.equals("ROLE_MENTOR"))
			addSameSiteCookie(response, createCookie("loginStatus", "main"));

		response.setStatus(HttpStatus.OK.value());
		response.sendRedirect("https://cogo.life/swagger-ui/index.html");
	}

	private Cookie createCookie(String key, String value) {
		Cookie cookie = new Cookie(key, value);
		cookie.setMaxAge(24 * 60 * 60);  // 24시간
		cookie.setSecure(true);  // https에서만 쿠키가 사용되게끔 설정
		cookie.setPath("/");    // 전역에서 쿠키가 보이게끔 설정
		cookie.setHttpOnly(true);  // JS가 쿠키를 가져가지 못하게 HTTPOnly 설정
		return cookie;
	}

	private void addSameSiteCookie(HttpServletResponse response, Cookie cookie) {
		StringBuilder cookieString = new StringBuilder();
		cookieString.append(cookie.getName()).append("=").append(cookie.getValue()).append("; ");
		cookieString.append("Max-Age=").append(cookie.getMaxAge()).append("; ");
		cookieString.append("Path=").append(cookie.getPath()).append("; ");
		cookieString.append("HttpOnly; ");
		cookieString.append("SameSite=None; ");
		cookieString.append("Secure");

		response.addHeader("Set-Cookie", cookieString.toString());
	}
}
