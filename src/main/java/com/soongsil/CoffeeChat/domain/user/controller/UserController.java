package com.soongsil.CoffeeChat.domain.user.controller;

import com.soongsil.CoffeeChat.domain.mentee.dto.MenteeRequest.MenteeJoinRequest;
import com.soongsil.CoffeeChat.domain.mentee.dto.MenteeResponse.MenteeInfoResponse;
import com.soongsil.CoffeeChat.domain.mentor.dto.MentorRequest.MentorJoinRequest;
import com.soongsil.CoffeeChat.domain.mentor.dto.MentorResponse.MentorInfoResponse;
import com.soongsil.CoffeeChat.domain.user.dto.UserRequest;
import com.soongsil.CoffeeChat.domain.user.dto.UserRequest.UserUpdateRequest;
import com.soongsil.CoffeeChat.domain.user.dto.UserResponse.User2FACodeResponse;
import com.soongsil.CoffeeChat.domain.user.dto.UserResponse.UserInfoResponse;
import com.soongsil.CoffeeChat.domain.user.service.UserService;
import com.soongsil.CoffeeChat.global.annotation.CurrentUsername;
import com.soongsil.CoffeeChat.global.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;

import static com.soongsil.CoffeeChat.global.uri.RequestUri.USER_URI;

@RestController
@RequestMapping(USER_URI)
@RequiredArgsConstructor
@Tag(name = "USER", description = "유저 관련 api")
public class UserController {
    private final UserService userService;

    @PostMapping("/mentor")
    @Operation(summary = "멘토로 가입하기!")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "성공!")
    public ResponseEntity<ApiResponse<MentorInfoResponse>> joinWithMentor(
            @RequestBody MentorJoinRequest dto,
            @Parameter(hidden = true) @CurrentUsername String username) {
        MentorInfoResponse mentorInfoResponse =
                userService.registerMentor(username, dto);
        return ResponseEntity.created(URI.create(USER_URI + "/" + "mentor"))
                .body(ApiResponse.onSuccessCREATED(mentorInfoResponse));
    }

    @PostMapping("/mentee")
    @Operation(summary = "멘티로 가입하기!")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "성공!")
    public ResponseEntity<ApiResponse<MenteeInfoResponse>> joinWithMentee(
            @RequestBody MenteeJoinRequest dto,
            @Parameter(hidden = true) @CurrentUsername String username) {
        return ResponseEntity.created(URI.create(USER_URI + "/" + "mentee"))
                .body(ApiResponse.onSuccessCREATED(userService.registerMentee(username, dto)));
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
            @RequestBody UserUpdateRequest dto,
            @Parameter(hidden = true) @CurrentUsername String username) {
        return ResponseEntity.ok().body(ApiResponse.onSuccessOK(userService.updateUser(dto, username)));
    }

    @GetMapping()
    @Operation(summary = "기본정보 조회")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공!")
    public ResponseEntity<ApiResponse<UserRequest.UserGetRequest>> getUserInfo(
            @Parameter(hidden = true) @CurrentUsername String username) {
        return ResponseEntity.ok().body(ApiResponse.onSuccessOK(userService.getUser(username)));
    }

    @DeleteMapping()
    @Operation(summary = "탈퇴하기")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공!")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@Parameter(hidden = true) @CurrentUsername String username) {
        userService.deleteUser(username);
        return ResponseEntity.ok().body(ApiResponse.onSuccessOK(null));
    }

    @PostMapping(value = "/picture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "유저 사진 등록하기")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "성공!")
    public ResponseEntity<ApiResponse<UserInfoResponse>> updatePicture(
            @RequestPart(value = "image") MultipartFile image,
            @Parameter(hidden = true) @CurrentUsername String username) {
        return ResponseEntity.created(URI.create(USER_URI + "/" + "picture"))
                .body(ApiResponse.onSuccessCREATED(userService.uploadPicture(image, username)));
    }
}
