package com.soongsil.CoffeeChat.domain.push;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;

import org.springframework.stereotype.Service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.soongsil.CoffeeChat.domain.user.entity.User;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Entity
@Table(
        name = "device_token",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "token"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeviceToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 255)
    private String token;

    // flutter 에서 발급하는데 필요한가 ?
    @Enumerated(EnumType.STRING)
    private Platform platform;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public static DeviceToken of(User user, String token, Platform platform) {
        DeviceToken deviceToken = new DeviceToken();
        deviceToken.user = user;
        deviceToken.token = token;
        deviceToken.platform = platform;
        deviceToken.updatedAt = LocalDateTime.now();
        return deviceToken;
    }

    public void touch() {
        this.updatedAt = LocalDateTime.now();
    }

    @Service
    @RequiredArgsConstructor
    @Slf4j
    public static class NotificationService {

        private final DeviceTokenRepository deviceTokenRepository;

        public void sendChatPush(User receiver, Long roomId, String title, String body) {

            List<DeviceToken> tokens = deviceTokenRepository.findAllByUserId(receiver.getId());
            if (tokens.isEmpty()) return;

            for (DeviceToken t : tokens) {
                try {
                    Message message =
                            Message.builder()
                                    .setToken(t.getToken())
                                    .setNotification(
                                            Notification.builder()
                                                    .setTitle(title)
                                                    .setBody(body)
                                                    .build())
                                    .putData("type", "CHAT")
                                    .putData("roomId", String.valueOf(roomId))
                                    .build();

                    FirebaseMessaging.getInstance().send(message);
                } catch (Exception e) {
                    // TODO: Token 만료/무효일 시 삭제
                    log.warn(
                            "FCM push failed. userId={}, token={}",
                            receiver.getId(),
                            t.getToken(),
                            e);
                }
            }
        }
    }
}
