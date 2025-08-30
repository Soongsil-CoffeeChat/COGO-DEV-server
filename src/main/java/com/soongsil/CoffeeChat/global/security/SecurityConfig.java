package com.soongsil.CoffeeChat.global.security;

import java.util.Arrays;
import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;

import com.soongsil.CoffeeChat.domain.auth.repository.RefreshRepository;
import com.soongsil.CoffeeChat.global.security.filter.AuthExceptionHandlingFilter;
import com.soongsil.CoffeeChat.global.security.filter.CustomLogoutFilter;
import com.soongsil.CoffeeChat.global.security.filter.JwtAuthenticationFilter;
import com.soongsil.CoffeeChat.global.security.handler.CustomSuccessHandler;
import com.soongsil.CoffeeChat.global.security.handler.JwtAccessDeniedHandler;
import com.soongsil.CoffeeChat.global.security.handler.JwtAuthenticationEntryPoint;
import com.soongsil.CoffeeChat.global.security.jwt.JwtUtil;
import com.soongsil.CoffeeChat.global.security.oauth2.CustomOAuth2UserService;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomSuccessHandler customSuccessHandler;
    private final JwtUtil jwtUtil;
    private final RefreshRepository refreshRepository;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final AuthExceptionHandlingFilter authExceptionHandlingFilter;

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
//                                                            "https://back-coffeego.com/auth/login/apple/callback",
                                                            "https://soongsil-coffeechat.github.io"));
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
                .csrf(csrf -> csrf.disable())
                .formLogin(formLogin -> formLogin.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                .exceptionHandling(
                        exception ->
                                exception
                                        .authenticationEntryPoint(
                                                jwtAuthenticationEntryPoint) // 401
                                        .accessDeniedHandler(jwtAccessDeniedHandler) // 403
                        )
                .oauth2Login(
                        oauth2 ->
                                oauth2.userInfoEndpoint(
                                                userInfo ->
                                                        userInfo.userService(
                                                                customOAuth2UserService) // OAuth2:
                                                // naver/kakao/구글(OAuth2)
                                                )
                                        .successHandler(customSuccessHandler))
                .authorizeHttpRequests(
                        auth ->
                                auth.requestMatchers(HttpMethod.OPTIONS, "/**")
                                        .permitAll()
                                        .requestMatchers(
                                                "/v3/api-docs/**",
                                                "/swagger-ui/**",
                                                "/swagger-resources/**",
                                                "/health-check",
                                                "/",
                                                "/security-check",
                                                "/reissue",
                                                "/auth/reissue",
                                                "/auth/login/**")
                                        .permitAll()
                                        .requestMatchers("/api/v2/admin/**")
                                        .permitAll()
                                        .requestMatchers(
                                                HttpMethod.GET, "/api/v2/mentors/{mentorId}/**")
                                        .permitAll()
                                        .requestMatchers(HttpMethod.GET, "/api/v2/mentors/part")
                                        .permitAll()
                                        .requestMatchers("/api/v2/possibleDates/**")
                                        .hasAnyRole("MENTOR", "MENTEE")
                                        .requestMatchers("/api/v2/mentors/**")
                                        .hasAnyRole("MENTOR", "MENTEE")
                                        .requestMatchers("/api/v2/applications/**")
                                        .hasAnyRole("MENTOR", "MENTEE")
                                        .requestMatchers("/api/v2/chat/**")
                                        .hasAnyRole("MENTOR", "MENTEE")
                                        .requestMatchers("/ws/**")
                                        .permitAll()
                                        .anyRequest()
                                        .authenticated())
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(
                        new CustomLogoutFilter(jwtUtil, refreshRepository), LogoutFilter.class)
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtUtil),
                        UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(authExceptionHandlingFilter, JwtAuthenticationFilter.class);

        return http.build();
    }
}
