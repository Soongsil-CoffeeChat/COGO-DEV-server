package com.soongsil.CoffeeChat.config;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
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
import lombok.RequiredArgsConstructor;



import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
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
        this.customOAuth2UserService = customOAuth2UserService;
        this.customSuccessHandler = customSuccessHandler;
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
        hierarchy.setHierarchy("ROLE_ADMIN > ROLE_MENTEE" + "ROLE_ADMIN > ROLE_MENTOR\n" +
            "ROLE_MENTEE > ROLE_USER" + "ROLE_MENTOR > ROLE_USER");
        return hierarchy;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(corsCustomizer -> corsCustomizer.configurationSource(request -> {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.setAllowedOrigins(Arrays.asList("https://localhost:3000", "http://localhost:8080", "http://localhost:3000",
                        "https://cogo.life", "https://coffeego-ssu.web.app")); // 프론트 서버의 주소들 // 프론트 서버의 주소
                configuration.setAllowedMethods(Collections.singletonList("*"));  // 모든 요청 메서드 허용
                configuration.setAllowCredentials(true);
                configuration.setAllowedHeaders(Collections.singletonList("*"));  // 모든 헤더 허용
                configuration.setMaxAge(3600L);
                configuration.setExposedHeaders(Arrays.asList("Set-Cookie", "Authorization", "Access", "loginStatus")); // Set-Cookie 및 Authorization 헤더 노출
                return configuration;
            }))
            .csrf(csrf -> csrf.disable())  // CSRF 비활성화
            .formLogin(formLogin -> formLogin.disable())  // 폼 로그인 비활성화
            .httpBasic(httpBasic -> httpBasic.disable())  // HTTP Basic 인증 비활성화
            .oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint.userService(customOAuth2UserService))
                .successHandler(customSuccessHandler))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()  // 모든 OPTIONS 요청에 대해 인증을 요구하지 않음
                .requestMatchers("/health-check", "/", "/reissue", "/security-check").permitAll()
                .requestMatchers("/api/v1/user/**", "/auth/**").hasAnyRole("USER", "MENTOR", "MENTEE")
                .requestMatchers("/api/v1/possibleDate/**").hasAnyRole("MENTOR", "MENTEE")
                .requestMatchers("/api/v1/mentor/**").hasAnyRole("MENTOR", "MENTEE")
                .anyRequest().authenticated())
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // 세션 정책을 STATELESS로 설정
            .addFilterBefore(new CustomLogoutFilter(jwtUtil, refreshRepository), LogoutFilter.class)
            .addFilterAfter(new JWTFilter(jwtUtil), OAuth2LoginAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
            .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-resources/**");
    }
}




/*
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
    public RoleHierarchy roleHierarchy() {

        RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();

        hierarchy.setHierarchy("ROLE_ADMIN > ROLE_MENTEE" +"ROLE_ADMIN > ROLE_MENTOR\n"+
            "ROLE_MENTEE > ROLE_USER" + "ROLE_MENTOR > ROLE_USER");

        return hierarchy;
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http)throws Exception{
        http
            .cors(corsCustomizer -> corsCustomizer.configurationSource(request -> {

                CorsConfiguration configuration = new CorsConfiguration();

                configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000")); //프론트 서버의 주소
                configuration.setAllowedMethods(Collections.singletonList("*"));  //GET, POST, PUT등 모든 요청 허용
                configuration.setAllowCredentials(true);
                configuration.setAllowedHeaders(Collections.singletonList("*"));  //모든 헤더 허용
                configuration.setMaxAge(3600L);

                configuration.setExposedHeaders(
                    Collections.singletonList("Set-Cookie"));  //우리가 줄 데이터를 웹페이지에서 보이게 하기
                configuration.setExposedHeaders(Collections.singletonList("Authorization"));

                return configuration;
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

        //기본으로 설정되어있는 LogoutFilter 바로 앞에 커스텀한 LogoutFilter 추가
        http
                .addFilterBefore(new CustomLogoutFilter(jwtUtil, refreshRepository), LogoutFilter.class);
        http
                .addFilterAfter(new JWTFilter(jwtUtil), OAuth2LoginAuthenticationFilter.class);
        //oauth2로그인 (인증이 완료되면 리소스 서버로부터 데이터를 받아서 OAuth2UserService로 전달)
        //로그인 성공시 customSuccessHandler 호출
        http
                .oauth2Login((oauth2) -> oauth2
                        .userInfoEndpoint((userInfoEndpointConfig) -> userInfoEndpointConfig
                                .userService(customOAuth2UserService))
                        .successHandler(customSuccessHandler)
                );
        http
                .addFilterBefore(new CustomLogoutFilter(jwtUtil, refreshRepository), LogoutFilter.class);

        //경로별 인가 작업
        http    //기본경로 "/" 제외한 나머지는 로그인해야만 사용가능
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()  //preflight처리
                        .requestMatchers("/health-check", "/", "reissue").permitAll()
                        .requestMatchers("/api/v1/user/**", "auth/**").hasRole("USER")
                        //.requestMatchers("/api/v1/**").hasAnyRole("MENTEE", "MENTOR") //로그인 제외하면 다 멘티나 멘토 아니면 접근불가
                        .requestMatchers("api/v1/possibleDate/**").hasRole("MENTOR")
                        .requestMatchers("api/v1/mentor/**").hasRole("MENTEE")
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
*/