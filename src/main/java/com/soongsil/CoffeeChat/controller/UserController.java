package com.soongsil.CoffeeChat.controller;

import static com.soongsil.CoffeeChat.enums.RequestUri.USER_URI;

import java.net.URI;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.soongsil.CoffeeChat.controller.handler.ApiResponseGenerator;
import com.soongsil.CoffeeChat.dto.*;
import com.soongsil.CoffeeChat.dto.MenteeRequest.MenteeJoinRequest;
import com.soongsil.CoffeeChat.dto.MenteeResponse.*;
import com.soongsil.CoffeeChat.dto.MentorRequest.*;
import com.soongsil.CoffeeChat.dto.MentorResponse.*;
import com.soongsil.CoffeeChat.dto.UserController.UserInfoDto;
import com.soongsil.CoffeeChat.repository.User.UserRepository;
import com.soongsil.CoffeeChat.security.dto.CustomOAuth2User;
import com.soongsil.CoffeeChat.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(USER_URI)
@RequiredArgsConstructor
@Tag(name = "USER", description = "유저 관련 api")
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;

    // TODO: 이 getUserNameByAuthentication 함수가 모든 controller에서 중복되므로 util로 빼기
    private String getUserNameByAuthentication(Authentication authentication) throws Exception {
        CustomOAuth2User principal = (CustomOAuth2User) authentication.getPrincipal();
        if (principal == null) throw new Exception(); // TODO : Exception 만들기
        return principal.getUsername();
    }

    @PostMapping()
    @Operation(summary = "기본정보 기입")
    @ApiResponse(responseCode = "201", description = "성공!")
    public ResponseEntity<ApiResponseGenerator<UserInfoDto>> joinWithMentor(
            Authentication authentication, @RequestBody UserJoinRequestDto dto) throws Exception {
        UserInfoDto userInfoDto =
                userService.saveUserInformation(getUserNameByAuthentication(authentication), dto);
        return ResponseEntity.created(URI.create(USER_URI))
                .body(ApiResponseGenerator.onSuccessCREATED(userInfoDto));
    }

    @PostMapping("/mentor")
    @Operation(summary = "멘토로 가입하기!")
    @ApiResponse(responseCode = "201", description = "성공!")
    public ResponseEntity<ApiResponseGenerator<MentorInfoResponse>> joinWithMentor(
            Authentication authentication, @RequestBody MentorJoinRequest dto) throws Exception {
        MentorInfoResponse mentorInfoResponse =
                userService.saveMentorInformation(getUserNameByAuthentication(authentication), dto);
        return ResponseEntity.created(URI.create(USER_URI + "/" + "mentor"))
                .body(ApiResponseGenerator.onSuccessCREATED(mentorInfoResponse));
    }

    @PostMapping("/mentee")
    @Operation(summary = "멘티로 가입하기!")
    @ApiResponse(responseCode = "201", description = "성공!")
    public ResponseEntity<ApiResponseGenerator<MenteeInfoResponse>> joinWithMentee(
            Authentication authentication, @RequestBody MenteeJoinRequest dto) throws Exception {
        MenteeInfoResponse response =
                userService.saveMenteeInformation(getUserNameByAuthentication(authentication), dto);
        return ResponseEntity.created(URI.create(USER_URI + "/" + "mentee"))
                .body(ApiResponseGenerator.onSuccessCREATED(response));
    }

    @PutMapping("/picture")
    @Operation(summary = "이미지 저장하기")
    @ApiResponse(responseCode = "201", description = "성공!")
    public ResponseEntity<ApiResponseGenerator<UserInfoDto>> saveUserPicture(
            Authentication authentication, @RequestBody String picture) throws Exception {
        UserInfoDto userInfoDto =
                userService.saveUserPicture(getUserNameByAuthentication(authentication), picture);
        return ResponseEntity.created(URI.create(USER_URI + "/" + "picture"))
                .body(ApiResponseGenerator.onSuccessCREATED(userInfoDto));
    }

    @GetMapping("/sms")
    @Operation(summary = "SMS 인증 요청하기", description = "전화번호로 SMS 인증번호를 요청합니다.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "인증 요청 성공",
                content =
                        @Content(
                                mediaType = "application/json",
                                schema =
                                        @Schema(
                                                example =
                                                        "{\n\"verificationCode\": \"1234\",\n\"message\": \"Verification code sent successfully\"\n}"))),
        @ApiResponse(
                responseCode = "400",
                description = "인증번호 전송 실패",
                content =
                        @Content(
                                mediaType = "application/json",
                                schema =
                                        @Schema(
                                                example =
                                                        "{\n\"message\": \"Failed to send verification code\"\n}")))
    })
    public ResponseEntity<ApiResponseGenerator<Map<String, String>>> getSmsCode(
            Authentication authentication, @RequestParam("phoneNum") String phoneNum) {
        return ResponseEntity.ok()
                .body(ApiResponseGenerator.onSuccessOK(userService.getSmsCode(phoneNum)));
    }

    @PatchMapping("/phone")
    @Operation(summary = "번호 저장하기")
    @ApiResponse(responseCode = "200", description = "성공!")
    public ResponseEntity<ApiResponseGenerator<PhoneNumUpdateDto>> saveUserPhone(
            Authentication authentication, @RequestParam("phoneNum") String phoneNum)
            throws Exception {
        return ResponseEntity.created(URI.create(USER_URI + "/phone"))
                .body(
                        ApiResponseGenerator.onSuccessOK(
                                userService.saveUserPhone(
                                        phoneNum, getUserNameByAuthentication(authentication))));
    }

    @PatchMapping("/email")
    @Operation(summary = "메일 저장하기")
    @ApiResponse(responseCode = "200", description = "성공!")
    public ResponseEntity<ApiResponseGenerator<UserInfoDto>> saveUserEmail(
            Authentication authentication, @RequestParam("email") String email) throws Exception {
        return ResponseEntity.created(URI.create(USER_URI + "/email"))
                .body(
                        ApiResponseGenerator.onSuccessOK(
                                userService.saveUserEmail(
                                        email, getUserNameByAuthentication(authentication))));
    }

    @PatchMapping()
    @Operation(summary = "사용자 정보 수정")
    @ApiResponse(responseCode = "200", description = "성공!")
    public ResponseEntity<ApiResponseGenerator<UserInfoDto>> saveUserEmail(
            Authentication authentication, @RequestBody UserUpdateDto dto) throws Exception {
        return ResponseEntity.created(URI.create(USER_URI + "/email"))
                .body(
                        ApiResponseGenerator.onSuccessOK(
                                userService.changeUserInfo(
                                        dto, getUserNameByAuthentication(authentication))));
    }

    @GetMapping()
    @Operation(summary = "기본정보 조회")
    @ApiResponse(responseCode = "200", description = "성공!")
    public ResponseEntity<ApiResponseGenerator<UserGetDto>> getUserInfo(
            Authentication authentication) throws Exception {
        return ResponseEntity.ok()
                .body(
                        ApiResponseGenerator.onSuccessOK(
                                userService.findUserInfo(
                                        getUserNameByAuthentication(authentication))));
    }

    @DeleteMapping()
    @Operation(summary = "탈퇴하기")
    @ApiResponse(responseCode = "200", description = "성공!")
    public ResponseEntity<ApiResponseGenerator<Void>> deleteUser(Authentication authentication)
            throws Exception {
        userService.deleteUser(getUserNameByAuthentication(authentication));
        return ResponseEntity.ok().body(ApiResponseGenerator.onSuccessOK(null));
    }
}
