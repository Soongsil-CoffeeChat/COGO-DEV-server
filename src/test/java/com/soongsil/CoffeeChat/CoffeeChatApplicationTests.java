package com.soongsil.CoffeeChat;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;

import jakarta.servlet.http.HttpServletRequest;

@SpringBootTest
class CoffeeChatApplicationTests {

	@Test
	void contextLoads() {
	}
	@Test
	void authenticationTest(Authentication authentication){
		System.out.println("authentication.getName() = " + authentication.getName());
	}

	public abstract class CorsUtils {
		public static boolean isPreFlightRequest(HttpServletRequest request) {
			return (HttpMethod.OPTIONS.matches(request.getMethod()) &&
				request.getHeader(HttpHeaders.ORIGIN) != null &&
				request.getHeader(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD) != null);
		}
	}

}
