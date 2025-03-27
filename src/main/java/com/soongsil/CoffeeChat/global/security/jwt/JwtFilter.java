package com.soongsil.CoffeeChat.global.security.jwt;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.soongsil.CoffeeChat.global.exception.GlobalErrorCode;
import com.soongsil.CoffeeChat.global.exception.GlobalException;
import com.soongsil.CoffeeChat.global.security.dto.UserDto;
import com.soongsil.CoffeeChat.global.security.oauth2.CustomOAuth2User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter { // 요청당 한번만 실행되면 됨
    private final JwtUtil jwtUtil; // JWT검증 위하여 주입

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 특정 경로들에 대해 필터 로직을 건너뛰도록 설정
        if (request.getMethod().equals(HttpMethod.OPTIONS.name())) {
            // OPTIONS 요청일 경우 필터 처리를 건너뛰고 다음 필터로 진행
            filterChain.doFilter(request, response);
            return;
        }
        String path = request.getRequestURI();
        if (path.startsWith("/health-check")
                || path.startsWith("/security-check")
                || path.startsWith("/auth/reissue")
                || path.startsWith("/login")
                || path.startsWith("/reissue")
                || path.startsWith("/oauth2")
                || path.matches("^/api/v2/mentors/\\d+$")
                || path.matches("^/api/v2/mentors/part$")
                || path.matches("/oauth2/authorization/google")
                || path.startsWith("/auth/issue/mobile")) {
            System.out.println("jwt필터 통과로직");
            filterChain.doFilter(request, response);
            return;
        }

        // 헤더에서 authorization키에 담긴 토큰을 꺼냄
        String accessToken = jwtUtil.resolveToken(request);

        // 토큰이 없다면 다음 필터로 넘김
        if (accessToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // 토큰 유효성 검증
        if (jwtUtil.validateToken(accessToken))
            throw new GlobalException(GlobalErrorCode.JWT_INVALID_TOKEN);

        // 토큰에서 username과 role 획득
        String username = jwtUtil.getUsername(accessToken);
        String role = jwtUtil.getRole(accessToken);

        log.info("[*] Current User: " + username);
        log.info("[*] Current User Role: " + role);

        // userDTO를 생성하여 값 set
        UserDto userDTO = new UserDto();
        userDTO.setUsername(username);
        userDTO.setRole(role);

        // UserDetails 혹은 OAuth2User에 회원 정보 객체 담기
        CustomOAuth2User customOAuth2User = new CustomOAuth2User(userDTO);

        // 스프링 시큐리티 인증 토큰 생성
        Authentication authToken =
                new UsernamePasswordAuthenticationToken(
                        customOAuth2User, null, customOAuth2User.getAuthorities());
        // 세션에 사용자 등록
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}
