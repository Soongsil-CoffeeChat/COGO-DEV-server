package com.soongsil.CoffeeChat.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.soongsil.CoffeeChat.config.jwt.JWTUtil;
import com.soongsil.CoffeeChat.controller.handler.ApiResponseGenerator;
import com.soongsil.CoffeeChat.service.CustomOAuth2UserService;
import com.soongsil.CoffeeChat.service.RefreshTokenService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController  //RestController=Controller+ResponseBody
@RequestMapping("/auth")
@Tag(name = "REFRESHTOKEN", description = "리프레쉬 토큰 관련 api")
@RequiredArgsConstructor
public class RefreshTokenController {  //Refresh토큰으로 Access토큰 발급 및 2차회원가입 컨트롤러
	private final JWTUtil jwtUtil;
	private final RefreshTokenService refreshTokenService;

	private final CustomOAuth2UserService oAuth2UserService;

	@PostMapping("/reissue")
	@Operation(summary = "리프레쉬 토큰으로 액세스 토큰 reissue")
	@ApiResponse(responseCode = "200", description = "헤더 : access, refresh, loginStatus")
	public ResponseEntity<ApiResponseGenerator<String>> reissue(HttpServletRequest request,
		HttpServletResponse response) {
		return ResponseEntity.ok().body(
			ApiResponseGenerator.onSuccessOK(
				refreshTokenService.reissueByRefreshToken(request, response)
			)
		);
	}

	@PostMapping("/reissue/mobile")
	@Operation(summary = "리소스 서버에서 받은 accessToken으로 서비스 accessToken 발급")
	@ApiResponse(responseCode = "200", description = "유효한 google accessToken으로 요청시 body로 ROLE_USER 토큰 반환")
	public ResponseEntity<ApiResponseGenerator<String>> issueAccessToken(@RequestParam String accessToken) {
		return ResponseEntity.ok().body(
			ApiResponseGenerator.onSuccessOK(
				oAuth2UserService.verifyGoogleToken(accessToken)
			)
		);
	}
}
