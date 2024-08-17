package com.soongsil.CoffeeChat.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.soongsil.CoffeeChat.util.email.EmailUtil;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/email")
public class EmailController {
	private final EmailUtil emailUtil;

	@GetMapping("/async")
	public long sendAuthenticationMailAsync(@RequestParam("email") String receiver) throws MessagingException, InterruptedException {
		return emailUtil.sendAuthenticationEmailWithTiming(receiver);
	}

	@GetMapping("/noAsync")
	public Long sendAuthenticationMailNoAsync(@RequestParam("email") String receiver) throws MessagingException, InterruptedException {
		return emailUtil.sendAuthenticationEmailNoAsnyc(receiver);
	}
}
