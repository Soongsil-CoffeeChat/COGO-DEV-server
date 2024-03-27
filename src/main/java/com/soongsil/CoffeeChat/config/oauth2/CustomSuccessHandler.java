package com.soongsil.CoffeeChat.config.oauth2;

import com.soongsil.CoffeeChat.config.jwt.JWTUtil;
import com.soongsil.CoffeeChat.dto.CustomOAuth2User;
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
import java.util.Iterator;

//로그인이 성공했을 때 받은 데이터들을 바탕으로 JWT발급을 위한 핸들러
@Component
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JWTUtil jwtUtil;
    public CustomSuccessHandler(JWTUtil jwtUtil){
        this.jwtUtil=jwtUtil;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        //OAuth2User
        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();

        String username = customUserDetails.getUsername();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();
        //토큰 생성
        String accessToken = jwtUtil.createJwt("access", username, role, 600000L);  //10분
        String refreshToken = jwtUtil.createJwt("refresh", username, role, 86400000L); //24시간
        //Access토큰은 헤더에, Refresh 토큰은 쿠키에 담아 보내기
        response.setHeader("access", accessToken);
        response.addCookie(createCookie("refresh", refreshToken));
        response.setStatus(HttpStatus.OK.value());  //200으로 프론트에 반환쳐주기

        response.sendRedirect("http://localhost:3000/");  //프론트의 url에 redirect
    }

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24*60*60);  //24시간
        //cookie.setSecure(true);  //https에서만 쿠키가 사용되게끔 설정
        cookie.setPath("/");    //전역에서 쿠키가 보이게끔 설정
        cookie.setHttpOnly(true);  //JS가 쿠키를 가져가지 못하게 HTTPOnly설정
        return cookie;
    }
}
