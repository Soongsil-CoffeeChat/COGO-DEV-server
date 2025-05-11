package com.soongsil.CoffeeChat.domain.fcmNotification.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.soongsil.CoffeeChat.domain.user.entity.User;
import com.soongsil.CoffeeChat.domain.user.repository.UserRepository;

@Service
public class FcmService {
    private final UserRepository userRepository;

    public FcmService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 토큰 저장
    @Transactional
    public String saveToken(Long userId, String token) {
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(
                                () ->
                                        new ResponseStatusException(
                                                HttpStatus.NOT_FOUND, "해당 사용자를 찾을 수 없습니다."));
        user.setFCMToken(token);
        return "토큰이 성공적으로 저장되었습니다.";
    }

    // 토큰으로 푸시알림 전송
    public void sendMessage(String token, String title, String body)
            throws FirebaseMessagingException {

        // 1) notification 객체 구성
        Notification notification = Notification.builder().setTitle(title).setBody(body).build();

        // 2) 메시지 객체에 notification과 토큰 설정
        Message message = Message.builder().setNotification(notification).setToken(token).build();

        // 3) 전송 요청
        String response = FirebaseMessaging.getInstance().send(message);
        System.out.println("Sent message: " + response);
    }
}
