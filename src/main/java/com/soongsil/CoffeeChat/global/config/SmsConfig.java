package com.soongsil.CoffeeChat.global.config;

import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SmsConfig {
    @Value("${coolsms.api-key}")
    private String smsKey;

    @Value("${coolsms.api-secret}")
    private String smsSecret;

    @Value("${coolsms.from}")
    private String from;

    @Bean
    public DefaultMessageService messageService() {
        return NurigoApp.INSTANCE.initialize(smsKey, smsSecret, "https://api.coolsms.co.kr");
    }

    @Bean
    public String from() {
        return from;
    }
}
