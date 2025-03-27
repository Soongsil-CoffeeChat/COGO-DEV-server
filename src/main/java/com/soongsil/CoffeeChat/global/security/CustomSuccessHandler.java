package com.soongsil.CoffeeChat.global.security;

import java.io.IOException;
import java.util.Date;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.soongsil.CoffeeChat.domain.entity.Refresh;
import com.soongsil.CoffeeChat.domain.repository.RefreshRepository;
import com.soongsil.CoffeeChat.global.security.jwt.JwtUtil;
import com.soongsil.CoffeeChat.global.security.oauth2.CustomOAuth2User;

@Component
@RequiredArgsConstructor
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    private void addRefreshEntity(String username, String refresh, Long expiredMs) {
        Date date = new Date(System.currentTimeMillis() + expiredMs);

        Refresh refreshEntity = new Refresh();
        refreshEntity.setUsername(username);
        refreshEntity.setRefresh(refresh);
        refreshEntity.setExpiration(date.toString());

        refreshRepository.save(refreshEntity);
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException {

        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();
        String username = customUserDetails.getUsername();
        String role = authentication.getAuthorities().iterator().next().getAuthority();

        String accessToken = jwtUtil.createJwt("access", username, role, 600000L);
        String refreshToken = jwtUtil.createJwt("refresh", username, role, 86400000L);

        addRefreshEntity(username, refreshToken, 86400000L);

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
