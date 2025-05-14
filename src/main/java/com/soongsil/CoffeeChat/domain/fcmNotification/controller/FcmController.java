package com.soongsil.CoffeeChat.domain.fcmNotification.controller;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.soongsil.CoffeeChat.domain.fcmNotification.dto.FcmMessageRequest;
import com.soongsil.CoffeeChat.domain.fcmNotification.service.FcmService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/fcm")
public class FcmController {

    private final FcmService fcmService;

    // FCM 토큰 저장 (헤더로 받음)
    @PostMapping(value = "/{userId}", headers = "FCM-TOKEN")
    public ResponseEntity<String> registerFcmToken(
            @PathVariable("userId") Long userId,
            @RequestHeader("FCM-TOKEN") String token) {

        fcmService.saveToken(userId, token);
        return ResponseEntity.ok("✅ FCM 토큰이 저장되었습니다.");
    }

    // 푸시 메시지 전송 (단일 사용자)
    @PostMapping("/{userId}/message")
    public ResponseEntity<String> sendPushMessage(
            @PathVariable("userId") Long userId,
            @Valid @RequestBody FcmMessageRequest request) {

        try {
            fcmService.sendMessage(request.token(), request.title(), request.body());
            return ResponseEntity.ok("✅ 푸시 메시지 전송 성공");
        } catch (FirebaseMessagingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("❌ 푸시 메시지 전송 실패: " + e.getMessage());
        }
    }
}
