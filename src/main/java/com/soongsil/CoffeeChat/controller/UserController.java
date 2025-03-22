package com.soongsil.CoffeeChat.controller;

import static com.soongsil.CoffeeChat.global.uri.RequestUri.USER_URI;

import java.net.URI;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.soongsil.CoffeeChat.dto.MenteeRequest.MenteeJoinRequest;
import com.soongsil.CoffeeChat.dto.MenteeResponse.MenteeInfoResponse;
import com.soongsil.CoffeeChat.dto.MentorRequest.MentorJoinRequest;
import com.soongsil.CoffeeChat.dto.MentorResponse.MentorInfoResponse;
import com.soongsil.CoffeeChat.dto.PhoneNumUpdateDto;
import com.soongsil.CoffeeChat.dto.UserRequest;
import com.soongsil.CoffeeChat.dto.UserRequest.UserJoinRequest;
import com.soongsil.CoffeeChat.dto.UserRequest.UserUpdateRequest;
import com.soongsil.CoffeeChat.dto.UserResponse.UserInfoResponse;
import com.soongsil.CoffeeChat.global.api.ApiResponse;
import com.soongsil.CoffeeChat.global.security.oauth2.CustomOAuth2User;
import com.soongsil.CoffeeChat.repository.User.UserRepository;
import com.soongsil.CoffeeChat.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "성공!")
    public ResponseEntity<ApiResponse<UserInfoResponse>> joinWithMentor(
            Authentication authentication, @RequestBody UserJoinRequest dto) throws Exception {
        UserInfoResponse response =
                userService.saveUserInformation(getUserNameByAuthentication(authentication), dto);
        return ResponseEntity.created(URI.create(USER_URI))
                .body(ApiResponse.onSuccessCREATED(response));
    }

    @PostMapping("/mentor")
    @Operation(summary = "멘토로 가입하기!")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "성공!")
    public ResponseEntity<ApiResponse<MentorInfoResponse>> joinWithMentor(
            Authentication authentication, @RequestBody MentorJoinRequest dto) throws Exception {
        MentorInfoResponse mentorInfoResponse =
                userService.saveMentorInformation(getUserNameByAuthentication(authentication), dto);
        return ResponseEntity.created(URI.create(USER_URI + "/" + "mentor"))
                .body(ApiResponse.onSuccessCREATED(mentorInfoResponse));
    }

    @PostMapping("/mentee")
    @Operation(summary = "멘티로 가입하기!")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "성공!")
    public ResponseEntity<ApiResponse<MenteeInfoResponse>> joinWithMentee(
            Authentication authentication, @RequestBody MenteeJoinRequest dto) throws Exception {
        MenteeInfoResponse response =
                userService.saveMenteeInformation(getUserNameByAuthentication(authentication), dto);
        return ResponseEntity.created(URI.create(USER_URI + "/" + "mentee"))
                .body(ApiResponse.onSuccessCREATED(response));
    }

    @PutMapping("/picture")
    @Operation(summary = "이미지 저장하기")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "성공!")
    public ResponseEntity<ApiResponse<UserInfoResponse>> saveUserPicture(
            Authentication authentication, @RequestBody String picture) throws Exception {
        UserInfoResponse userInfoResponse =
                userService.saveUserPicture(getUserNameByAuthentication(authentication), picture);
        return ResponseEntity.created(URI.create(USER_URI + "/" + "picture"))
                .body(ApiResponse.onSuccessCREATED(userInfoResponse));
    }

    @GetMapping("/sms")
    @Operation(summary = "SMS 인증 요청하기", description = "전화번호로 SMS 인증번호를 요청합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "인증 요청 성공",
                content =
                        @Content(
                                mediaType = "application/json",
                                schema =
                                        @Schema(
                                                example =
                                                        "{\n\"verificationCode\": \"1234\",\n\"message\": \"Verification code sent successfully\"\n}"))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
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
    public ResponseEntity<ApiResponse<Map<String, String>>> getSmsCode(
            Authentication authentication, @RequestParam("phoneNum") String phoneNum) {
        return ResponseEntity.ok().body(ApiResponse.onSuccessOK(userService.getSmsCode(phoneNum)));
    }

    @PatchMapping("/phone")
    @Operation(summary = "번호 저장하기")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공!")
    public ResponseEntity<ApiResponse<PhoneNumUpdateDto>> saveUserPhone(
            Authentication authentication, @RequestParam("phoneNum") String phoneNum)
            throws Exception {
        return ResponseEntity.created(URI.create(USER_URI + "/phone"))
                .body(
                        ApiResponse.onSuccessOK(
                                userService.saveUserPhone(
                                        phoneNum, getUserNameByAuthentication(authentication))));
    }

    @PatchMapping("/email")
    @Operation(summary = "메일 저장하기")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공!")
    public ResponseEntity<ApiResponse<UserInfoResponse>> saveUserEmail(
            Authentication authentication, @RequestParam("email") String email) throws Exception {
        return ResponseEntity.created(URI.create(USER_URI + "/email"))
                .body(
                        ApiResponse.onSuccessOK(
                                userService.saveUserEmail(
                                        email, getUserNameByAuthentication(authentication))));
    }

    @PatchMapping()
    @Operation(summary = "사용자 정보 수정")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공!")
    public ResponseEntity<ApiResponse<UserInfoResponse>> saveUserEmail(
            Authentication authentication, @RequestBody UserUpdateRequest dto) throws Exception {
        return ResponseEntity.created(URI.create(USER_URI + "/email"))
                .body(
                        ApiResponse.onSuccessOK(
                                userService.changeUserInfo(
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
                                userService.findUserInfo(
                                        getUserNameByAuthentication(authentication))));
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
