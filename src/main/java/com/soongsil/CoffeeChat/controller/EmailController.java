package com.soongsil.CoffeeChat.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.soongsil.CoffeeChat.email.EmailUtil;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/email")
public class EmailController {
	private final EmailUtil emailUtil;

	@GetMapping()
	public CompletableFuture<String> sendAuthenticationMail(@RequestParam("email") String receiver) throws MessagingException, InterruptedException {
		return emailUtil.sendAuthenticationEmail(receiver);
	}
}
