package com.soongsil.CoffeeChat.domain.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.soongsil.CoffeeChat.domain.dto.MobileTokenResponseDTO;
import com.soongsil.CoffeeChat.domain.dto.ReissueDto;
import com.soongsil.CoffeeChat.domain.service.RefreshTokenService;
import com.soongsil.CoffeeChat.global.api.ApiResponse;
import com.soongsil.CoffeeChat.global.security.oauth2.CustomOAuth2UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController // RestController=Controller+ResponseBody
@RequestMapping("/auth")
@Tag(name = "REFRESHTOKEN", description = "리프레쉬 토큰 관련 api")
@RequiredArgsConstructor
public class RefreshTokenController { // Refresh토큰으로 Access토큰 발급 및 2차회원가입 컨트롤러
    private final RefreshTokenService refreshTokenService;
    private final CustomOAuth2UserService oAuth2UserService;

    @PostMapping("/reissue")
    @Operation(summary = "리프레쉬 토큰으로 액세스 토큰 reissue", description = "멘토 혹은 멘티로 가입한 상태라면 ")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "헤더 : access, refresh, loginStatus")
    public ResponseEntity<ApiResponse<ReissueDto>> reissue(@RequestParam String refresh) {
        ReissueDto response = refreshTokenService.reissueByRefreshToken2(refresh);
        return ResponseEntity.ok().body(ApiResponse.onSuccessOK(response));
    }

    @PostMapping("/issue/mobile")
    @Operation(summary = "[MOBILE] google 서버에서 받은 accessToken으로 서비스 accessToken 발급")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "유효한 google accessToken으로 요청시 body로 ROLE_USER 토큰 반환")
    public ResponseEntity<ApiResponse<MobileTokenResponseDTO>> issueAccessToken(
            @RequestParam String accessToken, @RequestParam String name) {
        return ResponseEntity.ok()
                .body(
                        ApiResponse.onSuccessOK(
                                oAuth2UserService.verifyGoogleToken(accessToken, name)));
    }

    @PostMapping("/reissue/mobile")
    @Operation(summary = "[MOBILE] refreshToken으로 서비스 accessToken, refreshToken 재발급")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "유효한 refreshToken 요청시 body로 accessToken, refreshToken 반환")
    public ResponseEntity<ApiResponse<Map<String, String>>> reissueAccessTokenWithRefreshToken(
            @RequestHeader(required = true) String refreshToken) {
        return ResponseEntity.ok()
                .body(
                        ApiResponse.onSuccessOK(
                                refreshTokenService.reissueByRefreshTokenWithResponseBody(
                                        refreshToken)));
    }
}
