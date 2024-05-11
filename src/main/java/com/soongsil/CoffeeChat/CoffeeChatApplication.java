package com.soongsil.CoffeeChat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
	servers = {
		@Server(url = "/", description = "Default Server url")
	}
)
@SpringBootApplication
public class CoffeeChatApplication {

	public static void main(String[] args) {
		SpringApplication.run(CoffeeChatApplication.class, args);
	}

}
