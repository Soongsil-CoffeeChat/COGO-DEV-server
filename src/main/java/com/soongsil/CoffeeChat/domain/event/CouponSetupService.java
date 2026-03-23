package com.soongsil.CoffeeChat.domain.event;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponSetupService {
    private final StringRedisTemplate redisTemplate;

    public void loadCouponsToRedis(List<String> s3Urls) {
        String key = "event:coupons";
        redisTemplate.opsForList().rightPushAll(key, s3Urls);
    }
}
