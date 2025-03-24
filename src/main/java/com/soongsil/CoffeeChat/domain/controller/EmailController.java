package com.soongsil.CoffeeChat.domain.controller;

// import com.soongsil.CoffeeChat.infra.email.EmailUtil;

// @RestController
// @RequiredArgsConstructor
// @RequestMapping(EMAIL_URI)
// public class EmailController {
//	private final EmailUtil emailUtil;
//
//	@GetMapping()
//	@Operation(summary = "이메일 인증 코드 전송")
//	@ApiResponse(responseCode = "200", description = "이메일로 전송된 코드 반환")
//	public ResponseEntity<ApiResponse<Map<String, String>>> sendAuthenticationMail(
//		@RequestParam("email") String receiver) throws
//		MessagingException,
//		InterruptedException {
//		System.out.println("receiver = " + receiver);
//		return ResponseEntity.ok().body(
//			ApiResponse.onSuccessOK(
//				Map.of(
//					"code", emailUtil.sendAuthenticationEmail(receiver)
//				)
//			)
//		);
//	}
// }
