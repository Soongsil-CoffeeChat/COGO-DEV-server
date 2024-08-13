package com.soongsil.CoffeeChat.config.async;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class AsyncConfig {
	@Bean(name = "mailExecutor")
	public Executor mailExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(1);  // 기본 스레드 수
		executor.setMaxPoolSize(2);   // 최대 스레드 수
		executor.setQueueCapacity(50);  // 큐 용량
		executor.setThreadNamePrefix("MailExecutor-");  // 스레드 이름 접두사
		executor.initialize();
		return executor;
	}
}
