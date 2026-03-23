package com.soongsil.CoffeeChat.domain.event;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CouponService {
    private final StringRedisTemplate redisTemplate;
    private final RedissonClient redissonClient;

    //
}
