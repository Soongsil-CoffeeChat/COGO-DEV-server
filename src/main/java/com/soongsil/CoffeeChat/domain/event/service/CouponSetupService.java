package com.soongsil.CoffeeChat.domain.event.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CouponSetupService {
    private final StringRedisTemplate redisTemplate;

    public void setupCouponLimit(long maxCoupons) {
        redisTemplate.opsForValue().set("event:coupon:max", String.valueOf(maxCoupons));
        redisTemplate.opsForValue().set("event:coupon:seq", "0");
    }
}
