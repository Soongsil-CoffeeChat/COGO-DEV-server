package com.soongsil.CoffeeChat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.amazonaws.HttpMethod;

@Configuration
public class CorsMvcConfig implements WebMvcConfigurer { //컨트롤러에서 보내는 데이터를 받을수 있게끔


	@Override
	public void addCorsMappings(CorsRegistry corsRegistry) {

		corsRegistry.addMapping("/**")
			.allowedOrigins("http://localhost:3000")
			.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 명시적 메소드 허용
			.exposedHeaders("Set-Cookie");
	}
}
