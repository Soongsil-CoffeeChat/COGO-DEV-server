package com.soongsil.CoffeeChat.global.security.handler;

import java.io.IOException;
import java.util.Date;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.soongsil.CoffeeChat.domain.auth.entity.Refresh;
import com.soongsil.CoffeeChat.domain.auth.enums.Role;
import com.soongsil.CoffeeChat.domain.auth.repository.RefreshRepository;
import com.soongsil.CoffeeChat.global.security.jwt.JwtUtil;
import com.soongsil.CoffeeChat.global.security.oauth2.CustomOAuth2User;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    @Value("${spring.jwt.refresh-expiration}")
    private long refreshTokenExpiration;

    private void addRefreshEntity(String username, String refresh, Long expiredMs) {
        Date date = new Date(System.currentTimeMillis() + expiredMs);

        Refresh refreshEntity =
                Refresh.builder()
                        .username(username)
                        .refresh(refresh)
                        .expiration(date.toString())
                        .build();

        refreshRepository.save(refreshEntity);
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException {

        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();
        String username = customUserDetails.getUsername();
        Role role = Role.valueOf(authentication.getAuthorities().iterator().next().getAuthority());

        String refreshToken = jwtUtil.createRefreshToken(username, role);

        addRefreshEntity(username, refreshToken, refreshTokenExpiration);

        // Add Cookies
        addSameSiteCookie(response, "refresh", refreshToken);
        addSameSiteCookie(response, "loginStatus", role.equals("ROLE_USER") ? "signup" : "main");

        response.getHeaderNames()
                .forEach(header -> System.out.println(header + ": " + response.getHeader(header)));
        response.setStatus(HttpStatus.OK.value());
        String redirectUrl =
                String.format(
                        "https://coffeego-ssu.web.app/callback?refreshToken=%s&loginStatus=%s",
                        refreshToken, role.equals("ROLE_USER") ? "signup" : "main");
        response.sendRedirect(redirectUrl);
    }

    private void addSameSiteCookie(HttpServletResponse response, String name, String value) {
        ResponseCookie responseCookie =
                ResponseCookie.from(name, value)
                        .httpOnly(true)
                        .secure(true) // HTTPS에서만 전송
                        .sameSite("None") // 크로스 사이트 쿠키 허용
                        .domain(".coffeego-ssu.web.app") // 도메인 설정
                        .path("/") // 모든 경로에서 유효
                        .maxAge(24 * 60 * 60) // 1일 유효
                        .build();

        response.addHeader("Set-Cookie", responseCookie.toString());
        System.out.println("쿠키 : " + responseCookie.toString());
    }
}
