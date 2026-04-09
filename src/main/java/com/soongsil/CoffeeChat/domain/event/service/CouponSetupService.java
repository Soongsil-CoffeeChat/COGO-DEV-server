package com.soongsil.CoffeeChat.domain.event.service;

import java.util.Set;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CouponSetupService {
    private final StringRedisTemplate redisTemplate;

    public void setupCouponLimit(long maxCoupons) {

        // 쿠폰 수량 초기화
        redisTemplate.opsForValue().set("event:coupon:max", String.valueOf(maxCoupons));
        redisTemplate.opsForValue().set("event:coupon:seq", "0");

        // 발급 신청 내역 set 초기화
        redisTemplate.delete("event:issued:applications");

        // 유저별 참여 횟수 내역 초기화 (매칭 삭제)
        Set<String> userParticipationKeys = redisTemplate.keys("event:user:participation:*");
        if (userParticipationKeys != null && !userParticipationKeys.isEmpty()) {
            redisTemplate.delete(userParticipationKeys);
        }
    }
}
