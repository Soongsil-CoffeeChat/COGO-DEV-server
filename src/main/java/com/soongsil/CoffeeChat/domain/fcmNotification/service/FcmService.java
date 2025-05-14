package com.soongsil.CoffeeChat.domain.fcmNotification.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.*;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.soongsil.CoffeeChat.domain.user.entity.User;
import com.soongsil.CoffeeChat.domain.user.repository.UserRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class FcmService {

    private final UserRepository userRepository;

    @Value("${fcm.key.path}")
    private String fcmPrivateKeyPath;

    @Value("${fcm.key.scope}")
    private String firebaseScope;

    public FcmService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Firebase 초기화
    @PostConstruct
    public void initializeFirebase() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseOptions options = new FirebaseOptions.Builder()
                        .setCredentials(GoogleCredentials
                                .fromStream(new ClassPathResource(fcmPrivateKeyPath).getInputStream())
                                .createScoped(List.of(firebaseScope)))
                        .build();
                FirebaseApp.initializeApp(options);
                System.out.println("✅ Firebase 초기화 완료");
            }
        } catch (IOException e) {
            throw new RuntimeException("❌ Firebase 초기화 실패: " + e.getMessage());
        }
    }

    // 토큰 저장
    @Transactional
    public String saveToken(Long userId, String token) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 사용자를 찾을 수 없습니다."));
        user.setFCMToken(token);
        return "토큰이 성공적으로 저장되었습니다.";
    }

    // 단일 사용자: 토큰으로 푸시알림 전송
    public void sendMessage(String token, String title, String body) throws FirebaseMessagingException {
        Notification notification = Notification.builder().setTitle(title).setBody(body).build();
        Message message = Message.builder().setNotification(notification).setToken(token).build();
        String response = FirebaseMessaging.getInstance().send(message);
        System.out.println("📨 단일 메시지 전송 완료: " + response);
    }

    // 다수 사용자: 토큰으로 푸시알림 전송
    public void sendToMultipleMessage(List<String> tokens, String title, String body) {
        List<Message> messages = tokens.stream()
                .map(t -> Message.builder()
                        .setNotification(Notification.builder().setTitle(title).setBody(body).build())
                        .setToken(t)
                        .build())
                .toList();

        try {
            BatchResponse response = FirebaseMessaging.getInstance().sendAll(messages);
            System.out.println("📦 다중 푸시 전송 완료. 성공: " + response.getSuccessCount() + "건, 실패: " + response.getFailureCount() + "건");

            if (response.getFailureCount() > 0) {
                List<String> failTokenList = new ArrayList<>();
                for (int i = 0; i < response.getResponses().size(); i++) {
                    if (!response.getResponses().get(i).isSuccessful()) {
                        failTokenList.add(tokens.get(i));
                    }
                }
                System.out.println("❌ 유효하지 않은 FCM 토큰 목록: " + failTokenList);
            }
        } catch (FirebaseMessagingException e) {
            System.out.println("🔥 FCM 알림 전송 중 오류 발생: " + e.getMessage());
        }
    }
}
