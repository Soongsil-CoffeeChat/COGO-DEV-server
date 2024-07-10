package com.soongsil.CoffeeChat.config.jwt;

import java.io.IOException;
import java.io.PrintWriter;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.soongsil.CoffeeChat.dto.CustomOAuth2User;
import com.soongsil.CoffeeChat.dto.UserDTO;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

public class JWTFilter extends OncePerRequestFilter { //요청당 한번만 실행되면 됨
	private final JWTUtil jwtUtil;  //JWT검증 위하여 주입

	public JWTFilter(JWTUtil jwtUtil) {
		this.jwtUtil = jwtUtil;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		// 특정 경로들에 대해 필터 로직을 건너뛰도록 설정
		if (request.getMethod().equals(HttpMethod.OPTIONS.name())) {
			// OPTIONS 요청일 경우 필터 처리를 건너뛰고 다음 필터로 진행
			filterChain.doFilter(request, response);
			return;
		}
		String path = request.getRequestURI();
		if (path.startsWith("/health-check") || path.startsWith("/security-check") || path.startsWith("/reissue")) {
			System.out.println("jwt필터 통과로직");
			filterChain.doFilter(request, response);
			return;
		}

		// 헤더에서 authorization키에 담긴 토큰을 꺼냄
		String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
		//토큰꺼내기
		String accessToken=authorization.split(" ")[1];
		System.out.println("accessToken = " + accessToken);

		// 토큰이 없다면 다음 필터로 넘김
		if (accessToken == null) {

			filterChain.doFilter(request, response);

			return;
		}
		System.out.println("여기까진 들어옴");
		//토큰 소멸 시간 검증
		if (jwtUtil.isExpired(accessToken)) {
			System.out.println("token expired");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 에러 반환
			response.setContentType("application/json");
			response.getWriter().write("{\"error\": \"Access token expired\"}"); //응답 json에 error : access token expired메시지 작성
			filterChain.doFilter(request, response);

			//조건이 해당되면 메소드 종료 (필수)
			return;
		}
		else System.out.println("expired로직 건너감");
		System.out.println("여긴 실행됨?");

		//토큰에서 username과 role 획득
		String username = jwtUtil.getUsername(accessToken);
		String role = jwtUtil.getRole(accessToken);

		//userDTO를 생성하여 값 set
		UserDTO userDTO = new UserDTO();
		userDTO.setUsername(username);
		userDTO.setRole(role);

		//UserDetails 혹은 OAuth2User에 회원 정보 객체 담기
		CustomOAuth2User customOAuth2User = new CustomOAuth2User(userDTO);

		//스프링 시큐리티 인증 토큰 생성
		Authentication authToken = new UsernamePasswordAuthenticationToken(customOAuth2User, null,
			customOAuth2User.getAuthorities());
		//세션에 사용자 등록
		SecurityContextHolder.getContext().setAuthentication(authToken);

		filterChain.doFilter(request, response);
		//
	}
}
