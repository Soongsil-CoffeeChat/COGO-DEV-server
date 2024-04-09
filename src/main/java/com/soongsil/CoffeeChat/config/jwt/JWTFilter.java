package com.soongsil.CoffeeChat.config.jwt;

import java.io.IOException;
import java.io.PrintWriter;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.soongsil.CoffeeChat.dto.CustomOAuth2User;
import com.soongsil.CoffeeChat.dto.UserDTO;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JWTFilter extends OncePerRequestFilter { //요청당 한번만 실행되면 됨
	private final JWTUtil jwtUtil;  //JWT검증 위하여 주입

	public JWTFilter(JWTUtil jwtUtil) {
		this.jwtUtil = jwtUtil;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		// 헤더에서 access키에 담긴 토큰을 꺼냄
		String accessToken = request.getHeader("access");

		// 토큰이 없다면 다음 필터로 넘김 (권한이 필요 없는 요청들이 있을 수 있기 때문)
		if (accessToken == null) {

			filterChain.doFilter(request, response);

			return;
		}

		// 토큰 만료 여부 확인, 만료시 다음 필터로 넘기지 않음
		try {
			jwtUtil.isExpired(accessToken);
		} catch (ExpiredJwtException e) {

			//response body
			PrintWriter writer = response.getWriter();
			writer.print("access token expired");

			//response status code(상태반환: 다음 필터로 넘기지 않고 바로 응답 반환)
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); //401
			return;
		}

		// 토큰이 access인지 확인 (발급시 페이로드에 명시)
		String category = jwtUtil.getCategory(accessToken);

		if (!category.equals("access")) {

			//response body
			PrintWriter writer = response.getWriter();
			writer.print("invalid access token");

			//response status code(상태반환: 다음 필터로 넘기지 않고 바로 응답 반환)
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);//401
			return;
		}

		//토큰에서 username과 role 획득
		String username = jwtUtil.getUsername(accessToken);
		String role = jwtUtil.getRole(accessToken);

		//userDTO를 생성하여 값 set
		UserDTO userDTO = new UserDTO();
		userDTO.setUsername(username);
		userDTO.setRole(role);

		//UserDetails에 회원 정보 객체 담기
		CustomOAuth2User customOAuth2User = new CustomOAuth2User(userDTO);

		//스프링 시큐리티 인증 토큰 생성(로그인 진행)
		Authentication authToken = new UsernamePasswordAuthenticationToken(customOAuth2User, null,
			customOAuth2User.getAuthorities());
		//일시적인 세션에 사용자 등록(사용자 요청에 대해서 로그인 상태로 변환됨)
		SecurityContextHolder.getContext().setAuthentication(authToken);

		filterChain.doFilter(request, response); //다음 필터로 넘기기
	}
}
