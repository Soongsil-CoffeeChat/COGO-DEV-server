package com.soongsil.CoffeeChat.controller;

import static com.soongsil.CoffeeChat.enums.RequestUri.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.soongsil.CoffeeChat.config.jwt.JWTUtil;
import com.soongsil.CoffeeChat.service.RefreshTokenService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController  //RestController=Controller+ResponseBody
@Tag(name = "REFRESHTOKEN", description = "리프레쉬 토큰 관련 api")
public class RefreshTokenController {  //Refresh토큰으로 Access토큰 발급 및 2차회원가입 컨트롤러
	private final JWTUtil jwtUtil;
	private final RefreshTokenService refreshTokenService;

	public RefreshTokenController(JWTUtil jwtUtil, RefreshTokenService refreshTokenService) {
		this.jwtUtil = jwtUtil;
		this.refreshTokenService = refreshTokenService;
	}

	@PostMapping("/reissue")
	@Operation(summary = "리프레쉬 토큰으로 액세스 토큰 reissue")
	@ApiResponse(responseCode = "200", description = "헤더 : access, refresh, loginStatus")
	public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {
		return refreshTokenService.reissueByRefreshToken(request, response);
	}
}
