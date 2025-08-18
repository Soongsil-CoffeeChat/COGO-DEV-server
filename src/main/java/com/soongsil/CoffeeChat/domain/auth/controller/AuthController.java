package com.soongsil.CoffeeChat.domain.auth.controller;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.soongsil.CoffeeChat.domain.auth.dto.AuthTokenResponse;
import com.soongsil.CoffeeChat.domain.auth.service.AuthService;
import com.soongsil.CoffeeChat.global.api.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@Tag(name = "AUTH", description = "인증 관련 API")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/login/google")
    @Operation(summary = "구글 로그인", description = "구글 서버에서 받은 accessToken으로 서비스 토큰 발급 및 사용자 생성")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description =
                    "유효한 구글 accessToken 요청 시 계정 상태와 토큰 반환 (accountStatus = NEW_ACCOUNT, EXISTING_ACCOUNT, RESTORED_ACCOUNT)")
    public ResponseEntity<ApiResponse<AuthTokenResponse>> googleLogin(
            @RequestParam String accessToken) {
        return ResponseEntity.ok()
                .body(ApiResponse.onSuccessOK(authService.verifyGoogleToken(accessToken)));
    }

    // 1) 클라이언트가 Apple OAuth 화면에서 승인을 완료하면
    // 2) Apple이 이 콜백으로 authorization code를client-name: apple 보내줍니다.
    // @PostMapping("/login/apple/callback")
    @GetMapping("/login/apple/callback")
    @Operation(
            summary = "애플 로그인",
            description = "애플 OAuth 승인 후 authorization code로 서비스 토큰 발급 및 사용자 생성")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "유효한 애플 accessToken 요청 시 계정 상태와 토큰 반환")
    public ResponseEntity<ApiResponse<AuthTokenResponse>> appleCallback(
            @RequestParam("code") String code)
            throws IOException,
                    NoSuchAlgorithmException,
                    InvalidKeySpecException,
                    InvalidKeyException {

        AuthTokenResponse tokenResponse = authService.verifyAppleToken(code);

        logger.debug("▶ appleCallback 완료 – AuthTokenResponse=[{}]", tokenResponse);

        return ResponseEntity.ok(ApiResponse.onSuccessOK(tokenResponse));
    }

    @PostMapping("/reissue")
    @Operation(summary = "토큰 재발급", description = "리프레시 토큰으로 액세스 토큰과 리프레시 토큰 재발급합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "새로운 액세스 토큰과 리프레시 토큰 반환")
    public ResponseEntity<ApiResponse<AuthTokenResponse>> reissueToken(
            @RequestParam String refreshToken) {
        return ResponseEntity.ok()
                .body(ApiResponse.onSuccessOK(authService.reissueToken(refreshToken)));
    }
}
