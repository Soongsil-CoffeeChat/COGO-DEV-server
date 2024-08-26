package com.soongsil.CoffeeChat.controller;

import static com.soongsil.CoffeeChat.enums.RequestUri.*;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.soongsil.CoffeeChat.controller.handler.ApiResponseGenerator;
import com.soongsil.CoffeeChat.util.email.EmailUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(EMAIL_URI)
public class EmailController {
	private final EmailUtil emailUtil;

	@GetMapping()
	@Operation(summary = "이메일 인증 코드 전송")
	@ApiResponse(responseCode = "200", description = "이메일로 전송된 코드 반환")
	public ResponseEntity<ApiResponseGenerator<Map<String, CompletableFuture<String>>>> sendAuthenticationMail(
		@RequestParam("email") String receiver) throws
		MessagingException,
		InterruptedException {
		System.out.println("receiver = " + receiver);
		return ResponseEntity.ok().body(
			ApiResponseGenerator.onSuccessOK(
				Map.of(
					"code", emailUtil.sendAuthenticationEmail(receiver)
				)
			)
		);
	}
}
