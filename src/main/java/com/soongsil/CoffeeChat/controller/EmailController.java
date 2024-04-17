package com.soongsil.CoffeeChat.controller;

import static com.soongsil.CoffeeChat.enums.RequestUri.*;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.soongsil.CoffeeChat.email.EmailUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name="EMAIL", description = "Email 인증 관련 api")
public class EmailController {

	private final EmailUtil emailUtil;

	@GetMapping(EMAIL_URI)
	@Operation(summary = "이메일 인증하기")
	@ApiResponse(responseCode = "200", description = "인증번호 반환")
	public String sendAuthenticationMail(@RequestParam("email") String receiver) throws MessagingException {
		return emailUtil.sendAuthenticationEmail(receiver);
	}
}
