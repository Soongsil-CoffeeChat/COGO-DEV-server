package com.soongsil.CoffeeChat.config;

import java.util.Arrays;
import java.util.Collections;

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
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;

import com.soongsil.CoffeeChat.repository.RefreshRepository;
import com.soongsil.CoffeeChat.security.CustomOAuth2UserService;
import com.soongsil.CoffeeChat.security.handler.JwtAccessDeniedHandler;
import com.soongsil.CoffeeChat.security.handler.JwtAuthenticationEntryPoint;
import com.soongsil.CoffeeChat.security.jwt.CustomLogoutFilter;
import com.soongsil.CoffeeChat.security.jwt.JWTFilter;
import com.soongsil.CoffeeChat.security.jwt.JWTUtil;
import com.soongsil.CoffeeChat.security.oauth2.CustomSuccessHandler;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomSuccessHandler customSuccessHandler;
    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
        hierarchy.setHierarchy(
                """
                    ROLE_ADMIN > ROLE_MENTEE
                    ROLE_ADMIN > ROLE_MENTOR
                    ROLE_MENTEE > ROLE_USER
                    ROLE_MENTOR > ROLE_USER
                """);
        return hierarchy;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(
                        cors ->
                                cors.configurationSource(
                                        request -> {
                                            CorsConfiguration configuration =
                                                    new CorsConfiguration();

                                            configuration.setAllowedOrigins(
                                                    Arrays.asList(
                                                            "https://localhost:3000",
                                                            "http://localhost:8080",
                                                            "https://back-coffeego.com",
                                                            "https://coffeego-ssu.web.app",
                                                            "https://accounts.google.co.kr"));

                                            configuration.setAllowedMethods(
                                                    Arrays.asList(
                                                            "GET", "POST", "PUT", "DELETE", "PATCH",
                                                            "OPTIONS"));
                                            configuration.setAllowedHeaders(
                                                    Collections.singletonList("*"));
                                            configuration.setExposedHeaders(
                                                    Arrays.asList(
                                                            "Set-Cookie",
                                                            "Authorization",
                                                            "Access",
                                                            "loginStatus"));
                                            configuration.setAllowCredentials(true);
                                            configuration.setMaxAge(3600L);
                                            return configuration;
                                        }))
                .csrf(csrf -> csrf.disable()) // CSRF 비활성화
                .formLogin(formLogin -> formLogin.disable()) // 폼 로그인 비활성화
                .httpBasic(httpBasic -> httpBasic.disable()) // HTTP Basic 인증 비활성화
                .exceptionHandling(
                        exception ->
                                exception
                                        .authenticationEntryPoint(
                                                jwtAuthenticationEntryPoint) // 401 처리
                                        .accessDeniedHandler(jwtAccessDeniedHandler) // 403 처리
                        )
                .oauth2Login(
                        oauth2 ->
                                oauth2.userInfoEndpoint(
                                                userInfoEndpoint ->
                                                        userInfoEndpoint.userService(
                                                                customOAuth2UserService))
                                        .successHandler(customSuccessHandler))
                .authorizeHttpRequests(
                        auth ->
                                auth.requestMatchers(HttpMethod.OPTIONS, "/**")
                                        .permitAll()
                                        .requestMatchers(
                                                "/health-check",
                                                "/",
                                                "/auth/reissue/**",
                                                "/security-check",
                                                "/reissue")
                                        .permitAll()
                                        .requestMatchers(
                                                HttpMethod.GET, "/api/v2/mentors/{mentorId}/**")
                                        .permitAll()
                                        .requestMatchers(HttpMethod.GET, "/api/v2/mentors/part")
                                        .permitAll()
                                        .requestMatchers("/auth/reissue/mobile/**")
                                        .permitAll()
                                        .requestMatchers("/auth/issue/mobile/**")
                                        .permitAll()
                                        .requestMatchers("/api/v2/possibleDates/**")
                                        .hasAnyRole("MENTOR", "MENTEE")
                                        .requestMatchers("/api/v2/mentors/**")
                                        .hasAnyRole("MENTOR", "MENTEE")
                                        .requestMatchers("/api/v2/applications/**")
                                        .hasAnyRole("MENTOR", "MENTEE")
                                        .anyRequest()
                                        .authenticated())
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(
                        new CustomLogoutFilter(jwtUtil, refreshRepository), LogoutFilter.class)
                .addFilterAfter(new JWTFilter(jwtUtil), OAuth2LoginAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web ->
                web.ignoring()
                        .requestMatchers(
                                "/v3/api-docs/**", "/swagger-ui/**", "/swagger-resources/**");
    }
}
