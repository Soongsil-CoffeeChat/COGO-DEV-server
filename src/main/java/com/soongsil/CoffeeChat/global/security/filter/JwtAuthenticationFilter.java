package com.soongsil.CoffeeChat.global.security.filter;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.soongsil.CoffeeChat.global.security.dto.UserDto;
import com.soongsil.CoffeeChat.global.security.jwt.JwtUtil;
import com.soongsil.CoffeeChat.global.security.oauth2.CustomOAuth2User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter { // 요청당 한번만 실행되면 됨
    private final JwtUtil jwtUtil; // JWT검증 위하여 주입

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 헤더에서 authorization키에 담긴 토큰을 꺼냄
        String accessToken = jwtUtil.resolveToken(request);

        // 토큰이 없다면 다음 필터로 넘김
        if (accessToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // 토큰 유효성 검증
        jwtUtil.validateToken(accessToken);

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
