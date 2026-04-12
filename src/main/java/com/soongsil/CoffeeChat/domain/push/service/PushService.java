package com.soongsil.CoffeeChat.domain.push.service;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.soongsil.CoffeeChat.domain.push.dto.DeviceTokenRequest;
import com.soongsil.CoffeeChat.domain.push.entity.DeviceToken;
import com.soongsil.CoffeeChat.domain.push.repository.DeviceTokenRepository;
import com.soongsil.CoffeeChat.domain.user.entity.User;
import com.soongsil.CoffeeChat.domain.user.repository.UserRepository;
import com.soongsil.CoffeeChat.global.exception.GlobalErrorCode;
import com.soongsil.CoffeeChat.global.exception.GlobalException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PushService {

    private final DeviceTokenRepository deviceTokenRepository;
    private final UserRepository userRepository;

    public void registerToken(String username, DeviceTokenRequest request) {
        User user =
                userRepository
                        .findByUsernameAndIsDeletedFalse(username)
                        .orElseThrow(() -> new GlobalException(GlobalErrorCode.USER_NOT_FOUND));

        deviceTokenRepository
                .findByUserIdAndToken(user.getId(), request.getToken())
                .ifPresentOrElse(
                        DeviceToken::touch,
                        () ->
                                deviceTokenRepository.save(
                                        DeviceToken.of(
                                                user, request.getToken(), request.getPlatform())));
    }

    // 사용자 탈퇴 시, 토큰 unregister 이후 사용자 delete
    public void unregisterToken(String username, String token) {
        User user =
                userRepository
                        .findByUsernameAndIsDeletedFalse(username)
                        .orElseThrow(() -> new GlobalException(GlobalErrorCode.USER_NOT_FOUND));
        deviceTokenRepository.deleteByUserIdAndToken(user.getId(), token);
    }
}
