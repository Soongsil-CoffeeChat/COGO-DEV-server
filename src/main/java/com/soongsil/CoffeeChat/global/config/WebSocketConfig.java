package com.soongsil.CoffeeChat.global.config;

import com.soongsil.CoffeeChat.domain.auth.enums.Role;
import com.soongsil.CoffeeChat.domain.chat.repository.ChatRoomUserRepository;
import com.soongsil.CoffeeChat.domain.user.entity.User;
import com.soongsil.CoffeeChat.domain.user.repository.UserRepository;
import com.soongsil.CoffeeChat.global.exception.GlobalErrorCode;
import com.soongsil.CoffeeChat.global.exception.GlobalException;
import com.soongsil.CoffeeChat.global.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
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

import java.util.Collection;
import java.util.List;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private static final Logger log = LoggerFactory.getLogger(WebSocketConfig.class);
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final ChatRoomUserRepository chatRoomUserRepository;

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

                            if (accessor.getUser()
                                    instanceof UsernamePasswordAuthenticationToken auth) {
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

                                // SUBSCRIBE: 채팅방 구독 시 추가 권한 확인
                                if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
                                    String destination = accessor.getDestination();
                                    if (destination != null
                                            && destination.startsWith("/topic/room.")) {
                                        try {
                                            // "/topic/room.123" 형식에서 룸 ID 추출
                                            String roomIdStr =
                                                    destination.substring("/topic/room.".length());
                                            Long roomId = Long.parseLong(roomIdStr);

                                            // 사용자가 해당 채팅방의 참여자인지 확인
                                            String username = auth.getName();
                                            User user =
                                                    userRepository
                                                            .findByUsername(username)
                                                            .orElseThrow(
                                                                    () ->
                                                                            new GlobalException(
                                                                                    GlobalErrorCode
                                                                                            .USER_NOT_FOUND));

                                            boolean isParticipant =
                                                    chatRoomUserRepository
                                                            .findByChatRoomIdAndUserId(
                                                                    roomId, user.getId())
                                                            .isPresent();

                                            if (!isParticipant) {
                                                log.error(
                                                        "사용자가 채팅방의 참여자가 아님: "
                                                                + username
                                                                + ", 룸 ID: "
                                                                + roomId);
                                                return null; // 구독 거부
                                            }
                                        } catch (Exception e) {
                                            log.error("채팅방 구독 권한 확인 중 오류 발생: " + e.getMessage());
                                            return null; // 오류 발생 시 구독 거부
                                        }
                                    }
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
