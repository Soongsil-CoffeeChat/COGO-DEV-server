package com.soongsil.CoffeeChat.config.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {
	@Bean
	public OpenAPI openAPI() {

		Info info = new Info()
			.title("COGO API 문서")
			.description("이상 있으면 말씀 부탁드립니다.");

		// Security 스키마 설정
		SecurityScheme bearerAuth = new SecurityScheme()
			.type(SecurityScheme.Type.HTTP)
			.scheme("bearer")
			.bearerFormat("JWT")
			.in(SecurityScheme.In.HEADER)
			.name(HttpHeaders.AUTHORIZATION);

		// Security 요청 설정
		SecurityRequirement addSecurityItem = new SecurityRequirement();
		addSecurityItem.addList("JWT");

		return new OpenAPI()
			// Security 인증 컴포넌트 설정
			.components(new Components().addSecuritySchemes("JWT", bearerAuth))
			// API 마다 Security 인증 컴포넌트 설정
			.addSecurityItem(addSecurityItem)
			.info(info);
	}
}
