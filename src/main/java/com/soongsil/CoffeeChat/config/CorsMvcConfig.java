package com.soongsil.CoffeeChat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsMvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry corsRegistry) {
        corsRegistry
                .addMapping("/**")
                .allowedOrigins(
                        "https://localhost:3000",
                        "http://localhost:8080",
                        "https://back-coffeego.com",
                        "https://coffeego-ssu.web.app",
                        "https://accounts.google.co.kr")
                .allowedMethods("OPTIONS", "GET", "POST", "PUT", "DELETE", "PATCH")
                .allowedHeaders("*")
                .exposedHeaders("Set-Cookie", "Authorization", "loginStatus")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
