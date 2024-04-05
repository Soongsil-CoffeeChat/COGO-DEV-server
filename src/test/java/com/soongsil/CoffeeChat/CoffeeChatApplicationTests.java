package com.soongsil.CoffeeChat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;

@SpringBootTest
class CoffeeChatApplicationTests {

	@Test
	void contextLoads() {
	}
	@Test
	void authenticationTest(Authentication authentication){
		System.out.println("authentication.getName() = " + authentication.getName());
	}

}
