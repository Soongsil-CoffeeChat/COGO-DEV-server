package com.soongsil.CoffeeChat.config.queryDSL;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

//JPA Query Factory를 Bean으로 등록해두기
@Configuration
@RequiredArgsConstructor
public class QueryDSLConfig {
	private final EntityManager entityManager;

	@Bean
	public JPAQueryFactory jpaQueryFactory() {
		return new JPAQueryFactory(entityManager);
	}
}
