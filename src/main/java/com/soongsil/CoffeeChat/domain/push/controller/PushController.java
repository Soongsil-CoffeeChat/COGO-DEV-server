package com.soongsil.CoffeeChat.domain.push.controller;

import com.soongsil.CoffeeChat.domain.push.dto.DeviceTokenRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.soongsil.CoffeeChat.domain.push.service.PushService;

import lombok.RequiredArgsConstructor;

// 수정 필요_ throw 추가
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/push")
public class PushController {

    private final PushService pushService;

    @PostMapping("/tokens")
    public ResponseEntity<Void> register(
            Authentication authentication, @RequestBody DeviceTokenRequest request) {
        pushService.registerToken(authentication.getName(), request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/tokens")
    public ResponseEntity<Void> unregister(
            Authentication authentication, @RequestParam String token) {
        pushService.unregisterToken(authentication.getName(), token);
        return ResponseEntity.ok().build();
    }
}
