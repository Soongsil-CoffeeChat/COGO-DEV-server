package com.soongsil.CoffeeChat.controller;

import static com.soongsil.CoffeeChat.enums.RequestUri.*;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.soongsil.CoffeeChat.controller.handler.ApiResponseGenerator;
import com.soongsil.CoffeeChat.util.email.EmailUtil;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(EMAIL_URI)
public class EmailController {
	private final EmailUtil emailUtil;

	@GetMapping()
	public ResponseEntity<ApiResponseGenerator<Map<String, String>>> sendAuthenticationMail(
		@RequestParam("email") String receiver) throws
		MessagingException,
		InterruptedException {
		return ResponseEntity.ok(
			ApiResponseGenerator.onSuccessOK(
				Map.of(
					"code", emailUtil.sendAuthenticationEmail(receiver)
				)
			)
		);
	}
}
