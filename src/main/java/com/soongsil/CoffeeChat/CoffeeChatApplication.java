package com.soongsil.CoffeeChat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;


@SpringBootApplication
public class CoffeeChatApplication {

	public static void main(String[] args) {
		SpringApplication.run(CoffeeChatApplication.class, args);
	}

}
