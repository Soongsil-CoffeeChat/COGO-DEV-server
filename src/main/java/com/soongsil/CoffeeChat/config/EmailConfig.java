package com.soongsil.CoffeeChat.config;

// @Configuration
// public class EmailConfig {
//	@Value("${spring.mail.host}")
//	private String host;
//
//	@Value("${spring.mail.port}")
//	private int port;
//
//	@Value("${spring.mail.sender}")
//	private String sender;
//
//	@Value("${spring.mail.password}")
//	private String password;
//
//	@Value("${spring.mail.properties.mail.smtp.auth}")
//	private boolean auth;
//
//	@Value("${spring.mail.properties.mail.smtp.starttls.enable}")
//	private boolean enable;
//
//	@Bean
//	public JavaMailSender javaMailSender() {
//		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
//		mailSender.setHost(host);
//		mailSender.setPort(port);
//		mailSender.setUsername(sender);
//		mailSender.setPassword(password);
//
//		Properties props = mailSender.getJavaMailProperties();
//		props.put("mail.smtp.auth", auth);
//		props.put("mail.smtp.starttls.enable", enable);
//
//		return mailSender;
//	}
// }
