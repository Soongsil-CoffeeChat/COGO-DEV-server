package com.soongsil.CoffeeChat.global.config.data;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Value("${spring.data.redis.ssl.enabled:false}")
    private boolean redisSslEnabled;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        String prefix = redisSslEnabled ? "rediss://" : "redis://";
        config.useSingleServer().setAddress(prefix + redisHost + ":" + redisPort);
        return Redisson.create(config);
    }
}
