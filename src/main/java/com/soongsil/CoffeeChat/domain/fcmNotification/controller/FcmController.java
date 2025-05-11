package com.soongsil.CoffeeChat.domain.fcmNotification.controller;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.soongsil.CoffeeChat.domain.fcmNotification.dto.FcmMessageRequest;
import com.soongsil.CoffeeChat.domain.fcmNotification.dto.PostTokenRequest;
import com.soongsil.CoffeeChat.domain.fcmNotification.service.FcmService;

@RestController
@RequestMapping("/users")
public class FcmController {
    private final FcmService fcmService;

    public FcmController(FcmService fcmService) {
        this.fcmService = fcmService;
    }

    // FCM 토큰 저장
    @PostMapping("/{userID}/token")
    public ResponseEntity<String> saveToken(
            @PathVariable Long userID, @Valid @RequestBody PostTokenRequest req) {
        fcmService.saveToken(userID, req.getToken());
        return ResponseEntity.ok("토큰이 성공적으로 저장되었습니다.");
    }

    // FCM 토큰 저장 후 메시지 전송
    @PostMapping("/{userId}/message")
    public ResponseEntity<String> sendMessage(
            @PathVariable Long userId, @Valid @RequestBody FcmMessageRequest request) {
        try {
            fcmService.sendMessage(request.token(), request.title(), request.body());
            return ResponseEntity.ok("푸시 메시지 전송 성공");
        } catch (FirebaseMessagingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("푸시 메시지 전송 실패: " + e.getMessage());
        }
    }
}
