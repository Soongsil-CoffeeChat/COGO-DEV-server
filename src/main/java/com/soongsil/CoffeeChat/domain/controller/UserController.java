package com.soongsil.CoffeeChat.domain.controller;

import static com.soongsil.CoffeeChat.global.uri.RequestUri.USER_URI;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.soongsil.CoffeeChat.domain.dto.MenteeRequest.MenteeJoinRequest;
import com.soongsil.CoffeeChat.domain.dto.MenteeResponse.MenteeInfoResponse;
import com.soongsil.CoffeeChat.domain.dto.MentorRequest.MentorJoinRequest;
import com.soongsil.CoffeeChat.domain.dto.MentorResponse.MentorInfoResponse;
import com.soongsil.CoffeeChat.domain.dto.UserRequest;
import com.soongsil.CoffeeChat.domain.dto.UserRequest.UserUpdateRequest;
import com.soongsil.CoffeeChat.domain.dto.UserResponse.User2FACodeResponse;
import com.soongsil.CoffeeChat.domain.dto.UserResponse.UserInfoResponse;
import com.soongsil.CoffeeChat.domain.service.UserService;
import com.soongsil.CoffeeChat.global.api.ApiResponse;
import com.soongsil.CoffeeChat.global.security.oauth2.CustomOAuth2User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(USER_URI)
@RequiredArgsConstructor
@Tag(name = "USER", description = "유저 관련 api")
public class UserController {
    private final UserService userService;

    // TODO: 이 getUserNameByAuthentication 함수가 모든 controller에서 중복되므로 util로 빼기
    private String getUserNameByAuthentication(Authentication authentication) throws Exception {
        CustomOAuth2User principal = (CustomOAuth2User) authentication.getPrincipal();
        if (principal == null) throw new Exception(); // TODO : Exception 만들기
        return principal.getUsername();
    }

    @PostMapping("/mentor")
    @Operation(summary = "멘토로 가입하기!")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "성공!")
    public ResponseEntity<ApiResponse<MentorInfoResponse>> joinWithMentor(
            Authentication authentication, @RequestBody MentorJoinRequest dto) throws Exception {
        MentorInfoResponse mentorInfoResponse =
                userService.registerMentor(getUserNameByAuthentication(authentication), dto);
        return ResponseEntity.created(URI.create(USER_URI + "/" + "mentor"))
                .body(ApiResponse.onSuccessCREATED(mentorInfoResponse));
    }

    @PostMapping("/mentee")
    @Operation(summary = "멘티로 가입하기!")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "성공!")
    public ResponseEntity<ApiResponse<MenteeInfoResponse>> joinWithMentee(
            Authentication authentication, @RequestBody MenteeJoinRequest dto) throws Exception {
        MenteeInfoResponse response =
                userService.registerMentee(getUserNameByAuthentication(authentication), dto);
        return ResponseEntity.created(URI.create(USER_URI + "/" + "mentee"))
                .body(ApiResponse.onSuccessCREATED(response));
    }

    @GetMapping("/sms")
    @Operation(summary = "SMS 인증 요청하기", description = "전화번호로 SMS 인증번호를 요청합니다.")
    public ResponseEntity<ApiResponse<User2FACodeResponse>> getSmsCode(
            @RequestParam("phoneNum") String phoneNum) {
        return ResponseEntity.ok().body(ApiResponse.onSuccessOK(userService.send2FACode(phoneNum)));
    }

    @PatchMapping()
    @Operation(summary = "사용자 정보 수정")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공!")
    public ResponseEntity<ApiResponse<UserInfoResponse>> updateUser(
            Authentication authentication, @RequestBody UserUpdateRequest dto) throws Exception {
        return ResponseEntity.ok()
                .body(
                        ApiResponse.onSuccessOK(
                                userService.updateUser(
                                        dto, getUserNameByAuthentication(authentication))));
    }

    @GetMapping()
    @Operation(summary = "기본정보 조회")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공!")
    public ResponseEntity<ApiResponse<UserRequest.UserGetRequest>> getUserInfo(
            Authentication authentication) throws Exception {
        return ResponseEntity.ok()
                .body(
                        ApiResponse.onSuccessOK(
                                userService.getUser(getUserNameByAuthentication(authentication))));
    }

    @DeleteMapping()
    @Operation(summary = "탈퇴하기")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공!")
    public ResponseEntity<ApiResponse<Void>> deleteUser(Authentication authentication)
            throws Exception {
        userService.deleteUser(getUserNameByAuthentication(authentication));
        return ResponseEntity.ok().body(ApiResponse.onSuccessOK(null));
    }
}
