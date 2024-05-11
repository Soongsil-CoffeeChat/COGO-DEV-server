package com.soongsil.CoffeeChat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.amazonaws.HttpMethod;

@Configuration
public class CorsMvcConfig implements WebMvcConfigurer { //컨트롤러에서 보내는 데이터를 받을수 있게끔


	@Override
	public void addCorsMappings(CorsRegistry corsRegistry) {

		corsRegistry.addMapping("/**")  //모든 경로에서 매핑 진행
			.exposedHeaders("Set-Cookie")      //노출할 헤더값은 쿠키헤더
			.allowedOrigins("http://localhost:3000", "https://cogo.life/**");  //리액트서버주소에서 허용
	}
	/*
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
			.allowedOrigins("*")
			.allowedMethods(
			HttpMethod.GET.name(),
			HttpMethod.HEAD.name(),
			HttpMethod.POST.name(),
			HttpMethod.PUT.name(),
			HttpMethod.DELETE.name());
	}

	 */
}
