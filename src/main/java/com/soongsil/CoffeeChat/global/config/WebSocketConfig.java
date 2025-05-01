package com.soongsil.CoffeeChat.global.config;

import java.security.Principal;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.soongsil.CoffeeChat.domain.auth.enums.Role;
import com.soongsil.CoffeeChat.global.exception.GlobalErrorCode;
import com.soongsil.CoffeeChat.global.security.jwt.JwtUtil;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private static final Logger log = LoggerFactory.getLogger(WebSocketConfig.class);
    private final JwtUtil jwtUtil;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*");
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(
                new ChannelInterceptor() {

                    @Override
                    public Message<?> preSend(Message<?> message, MessageChannel channel) {
                        StompHeaderAccessor accessor =
                                MessageHeaderAccessor.getAccessor(
                                        message, StompHeaderAccessor.class);

                        System.out.println("\n\n" + message + "\n\n");
                        if (accessor == null) {
                            return message;
                        }

                        // 1. CONNECT: JWT 검증 후 인증 객체 설정
                        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                            String authHeader = accessor.getFirstNativeHeader("Authorization");
                            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                                try {
                                    String token = authHeader.substring(7);
                                    jwtUtil.validateToken(token);
                                    String username = jwtUtil.getUsername(token);
                                    Role role = jwtUtil.getRole(token);

                                    List<GrantedAuthority> authorities =
                                            List.of(new SimpleGrantedAuthority(role.name()));
                                    Authentication auth =
                                            new UsernamePasswordAuthenticationToken(
                                                    username, null, authorities);
                                    accessor.setUser(auth);
                                } catch (Exception e) {
                                    log.error(e.getMessage());
                                }
                            } else {
                                log.error(GlobalErrorCode.UNAUTHORIZED.getMessage());
                            }
                        }

                        // 2. SEND or SUBSCRIBE: 권한 확인
                        if (StompCommand.SEND.equals(accessor.getCommand())
                                || StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {

                            Principal user = accessor.getUser();
                            if (user instanceof UsernamePasswordAuthenticationToken auth) {
                                Collection<? extends GrantedAuthority> authorities =
                                        auth.getAuthorities();
                                boolean authorized =
                                        authorities.stream()
                                                .anyMatch(
                                                        grantedAuthority ->
                                                                grantedAuthority
                                                                                .getAuthority()
                                                                                .equals(
                                                                                        "ROLE_MENTEE")
                                                                        || grantedAuthority
                                                                                .getAuthority()
                                                                                .equals(
                                                                                        "ROLE_MENTOR")
                                                                        || grantedAuthority
                                                                                .getAuthority()
                                                                                .equals(
                                                                                        "ROLE_ADMIN"));

                                if (!authorized) {
                                    log.error(GlobalErrorCode.UNAUTHORIZED.getMessage());
                                }
                            } else {
                                log.error(GlobalErrorCode.UNAUTHORIZED.getMessage());
                            }
                        }

                        return message;
                    }
                });
    }
}
