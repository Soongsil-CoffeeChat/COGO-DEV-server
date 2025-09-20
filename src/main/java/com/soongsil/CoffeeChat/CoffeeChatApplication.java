package com.soongsil.CoffeeChat;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;

import java.util.TimeZone;

@OpenAPIDefinition(
        servers = {
            @Server(url = "https://back-coffeego.com", description = "Production server"),
            @Server(url = "http://localhost:8080", description = "Local development server")
        })
@SpringBootApplication
@EnableAsync
@EnableScheduling
public class CoffeeChatApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoffeeChatApplication.class, args);
    }

    @PostConstruct
    public void init(){
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
    }
}
