package com.soongsil.CoffeeChat.domain.push.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.soongsil.CoffeeChat.domain.push.DeviceToken;
import com.soongsil.CoffeeChat.domain.push.DeviceTokenRepository;
import com.soongsil.CoffeeChat.domain.user.entity.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final DeviceTokenRepository deviceTokenRepository;

    public void sendChatPush(User receiver, Long roomId, String title, String body) {
        List<DeviceToken> tokens = deviceTokenRepository.findAllByUserId(receiver.getId());
        if (tokens.isEmpty()) return;

        for (DeviceToken t : tokens) {
            try {
                Message msg =
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

                String response = FirebaseMessaging.getInstance().send(msg);
                log.info(
                        "[FCM] sent ok. receiverId={}, roomId={}, messageId={}",
                        receiver.getId(),
                        roomId,
                        response);

            } catch (FirebaseMessagingException e) {
                // TODO: 토큰 만료
                log.warn(
                        "[FCM] send fail. receiverId={}, roomId={}, code={}, msg={}",
                        receiver.getId(),
                        roomId,
                        e.getErrorCode(),
                        e.getMessage(),
                        e);
            }
        }
    }
}
