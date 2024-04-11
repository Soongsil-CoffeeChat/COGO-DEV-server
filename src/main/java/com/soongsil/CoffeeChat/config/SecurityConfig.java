package com.soongsil.CoffeeChat.config;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import com.soongsil.CoffeeChat.config.jwt.CustomLogoutFilter;
import com.soongsil.CoffeeChat.config.jwt.JWTFilter;
import com.soongsil.CoffeeChat.config.jwt.JWTUtil;
import com.soongsil.CoffeeChat.config.oauth2.CustomSuccessHandler;
import com.soongsil.CoffeeChat.repository.RefreshRepository;
import com.soongsil.CoffeeChat.service.CustomOAuth2UserService;

import jakarta.servlet.http.HttpServletRequest;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomSuccessHandler customSuccessHandler;
    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;
    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService,
                          CustomSuccessHandler customSuccessHandler,
                          JWTUtil jwtUtil,
                          RefreshRepository refreshRepository){
        this.customOAuth2UserService=customOAuth2UserService;
        this.customSuccessHandler=customSuccessHandler;
        this.jwtUtil=jwtUtil;
        this.refreshRepository=refreshRepository;
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http)throws Exception{
        http
                .cors(corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {

                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {

                        CorsConfiguration configuration = new CorsConfiguration();

                        configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000")); //프론트 서버의 주소
                        configuration.setAllowedMethods(Collections.singletonList("*"));  //GET, POST, PUT등 모든 요청 허용
                        configuration.setAllowCredentials(true);
                        configuration.setAllowedHeaders(Collections.singletonList("*"));  //모든 헤더 허용
                        configuration.setMaxAge(3600L);

                        configuration.setExposedHeaders(Collections.singletonList("Set-Cookie"));  //우리가 줄 데이터를 웹페이지에서 보이게 하기
                        configuration.setExposedHeaders(Collections.singletonList("Authorization"));

                        return configuration;
                    }
                }));
        //csrf disable : stateless이기 때문에 끄기
        http
                .csrf((auth) -> auth.disable());

        //From 로그인 방식 disable
        http
                .formLogin((auth) -> auth.disable());

        //HTTP Basic 인증 방식 disable
        http
                .httpBasic((auth) -> auth.disable());
        //특정 필터 이전에 JWTFilter 추가
        http
                .addFilterBefore(new JWTFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);
        //기본으로 설정되어있는 LogoutFilter 바로 앞에 커스텀한 LogoutFilter 추가
        http
                .addFilterBefore(new CustomLogoutFilter(jwtUtil, refreshRepository), LogoutFilter.class);


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
                        .requestMatchers("/reissue").permitAll()
                        //.requestMatchers("/api/v1/**").hasAnyRole("MENTEE", "MENTOR") //로그인 제외하면 다 멘티나 멘토 아니면 접근불가
                        .requestMatchers("/api/v1/**").hasRole("USER")
                        .anyRequest().authenticated());

        //세션 설정 : STATELESS (JWT로 인증 인가 사용할 것이므로)
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
            .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-resources/**");
    }
}
