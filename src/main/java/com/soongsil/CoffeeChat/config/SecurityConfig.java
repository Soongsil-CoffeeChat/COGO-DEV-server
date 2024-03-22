package com.soongsil.CoffeeChat.config;

import com.soongsil.CoffeeChat.config.jwt.JWTUtil;
import com.soongsil.CoffeeChat.config.oauth2.CustomSuccessHandler;
import com.soongsil.CoffeeChat.service.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomSuccessHandler customSuccessHandler;
    private final JWTUtil jwtUtil;
    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService,
                          CustomSuccessHandler customSuccessHandler,
                          JWTUtil jwtUtil){
        this.customOAuth2UserService=customOAuth2UserService;
        this.customSuccessHandler=customSuccessHandler;
        this.jwtUtil=jwtUtil;
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http)throws Exception{
        //csrf disable : stateless이기 때문에 끄기
        http
                .csrf((auth) -> auth.disable());

        //From 로그인 방식 disable
        http
                .formLogin((auth) -> auth.disable());

        //HTTP Basic 인증 방식 disable
        http
                .httpBasic((auth) -> auth.disable());

        //oauth2로그인 (인증이 완료되면 리소스 서버로부터 데이터를 받아서 OAuth2UserService로 전달)
        //로그인 성공시 customSuccessHandler 호출
        http
                .oauth2Login((oauth2) -> oauth2
                        .userInfoEndpoint((userInfoEndpointConfig) -> userInfoEndpointConfig
                                .userService(customOAuth2UserService))
                .successHandler(customSuccessHandler)
                );

        //경로별 인가 작업
        http    //기본경로 "/" 제외한 나머지는 로그인해야만 사용가능
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/").permitAll()
                        .anyRequest().authenticated());

        //세션 설정 : STATELESS (JWT로 인증 인가 사용할 것이므로)
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }
}
