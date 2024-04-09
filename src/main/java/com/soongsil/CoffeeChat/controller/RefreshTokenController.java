package com.soongsil.CoffeeChat.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.soongsil.CoffeeChat.config.jwt.JWTUtil;
import com.soongsil.CoffeeChat.service.RefreshTokenService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController  //RestController=Controller+ResponseBody
public class RefreshTokenController {  //Refresh토큰으로 Access토큰 발급 및 2차회원가입 컨트롤러
	private final JWTUtil jwtUtil;
	private final RefreshTokenService refreshTokenService;

	public RefreshTokenController(JWTUtil jwtUtil, RefreshTokenService refreshTokenService) {
		this.jwtUtil = jwtUtil;
		this.refreshTokenService = refreshTokenService;
	}

	@PostMapping("/reissue")
	public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {
		return refreshTokenService.reissueByRefreshToken(request, response);
	}
}
