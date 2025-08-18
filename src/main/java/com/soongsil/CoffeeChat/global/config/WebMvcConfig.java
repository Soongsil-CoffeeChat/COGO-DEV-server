package com.soongsil.CoffeeChat.global.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.soongsil.CoffeeChat.global.annotation.CurrentUsernameArgumentResolver;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final CurrentUsernameArgumentResolver currentUsernameArgumentResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(currentUsernameArgumentResolver);
    }
}
