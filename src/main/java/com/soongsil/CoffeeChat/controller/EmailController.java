package com.soongsil.CoffeeChat.controller;

import static com.soongsil.CoffeeChat.enums.RequestUri.*;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.soongsil.CoffeeChat.email.EmailUtil;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class EmailController {

	private final EmailUtil emailUtil;

	@GetMapping(EMAIL_URI)
	public String sendAuthenticationMail(@RequestParam("email") String receiver) throws MessagingException {
		return emailUtil.sendAuthenticationEmail(receiver);
	}
}
