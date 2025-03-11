package com.soongsil.CoffeeChat.controller;

import static com.soongsil.CoffeeChat.enums.RequestUri.*;

// import com.soongsil.CoffeeChat.util.email.EmailUtil;

// @RestController
// @RequiredArgsConstructor
// @RequestMapping(EMAIL_URI)
// public class EmailController {
//	private final EmailUtil emailUtil;
//
//	@GetMapping()
//	@Operation(summary = "이메일 인증 코드 전송")
//	@ApiResponse(responseCode = "200", description = "이메일로 전송된 코드 반환")
//	public ResponseEntity<ApiResponseGenerator<Map<String, String>>> sendAuthenticationMail(
//		@RequestParam("email") String receiver) throws
//		MessagingException,
//		InterruptedException {
//		System.out.println("receiver = " + receiver);
//		return ResponseEntity.ok().body(
//			ApiResponseGenerator.onSuccessOK(
//				Map.of(
//					"code", emailUtil.sendAuthenticationEmail(receiver)
//				)
//			)
//		);
//	}
// }
