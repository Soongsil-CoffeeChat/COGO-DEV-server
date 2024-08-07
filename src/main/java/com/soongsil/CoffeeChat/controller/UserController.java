package com.soongsil.CoffeeChat.controller;

import static com.soongsil.CoffeeChat.enums.RequestUri.*;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.soongsil.CoffeeChat.dto.CreateMenteeRequest;
import com.soongsil.CoffeeChat.dto.CreateMentorRequest;
import com.soongsil.CoffeeChat.dto.CustomOAuth2User;
import com.soongsil.CoffeeChat.entity.Mentee;
import com.soongsil.CoffeeChat.entity.Mentor;
import com.soongsil.CoffeeChat.entity.User;
import com.soongsil.CoffeeChat.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


import lombok.RequiredArgsConstructor;

import java.util.Map;

@RestController
@RequestMapping(USER_URI)
@RequiredArgsConstructor
@Tag(name="USER", description = "유저 관련 api")
public class UserController {
    private final UserService userService;

    private String getUserNameByAuthentication(Authentication authentication) throws Exception {
        CustomOAuth2User principal= (CustomOAuth2User)authentication.getPrincipal();
        if(principal==null) throw new Exception(); //TODO : Exception 만들기
        return principal.getUsername();
    }
    @PostMapping("/join/mentor")
    @Operation(summary="멘토로 가입하기!")
    @ApiResponse(responseCode = "200", description = "성공!")
    public ResponseEntity<Mentor> joinWithMentor(Authentication authentication,
                                                 @RequestBody CreateMentorRequest dto) throws Exception {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                userService.saveMentorInformation(getUserNameByAuthentication(authentication), dto)
        );
    }

    @PostMapping("/join/mentee")
    @Operation(summary="멘티로 가입하기!")
    @ApiResponse(responseCode = "200", description = "성공!")
    public ResponseEntity<Mentee> joinWithMentee(Authentication authentication,
                                                 @RequestBody CreateMenteeRequest dto) throws Exception {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                userService.saveMenteeInformation(getUserNameByAuthentication(authentication), dto)
        );
    }

    @PutMapping("/save/picture")
    @Operation(summary="이미지 저장하기")
    @ApiResponse(responseCode = "200", description = "성공!")
    public ResponseEntity<User> saveUserPicture(Authentication authentication,
                                                @RequestBody String picture) throws Exception {
        return ResponseEntity.ok(userService.saveUserPicture(getUserNameByAuthentication(authentication), picture));
    }

    @GetMapping("/sms")
    @Operation(summary = "SMS 인증 요청하기", description = "전화번호로 SMS 인증번호를 요청합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "인증 요청 성공",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = "{\n\"verificationCode\": \"1234\",\n\"message\": \"Verification code sent successfully\"\n}"))),
            @ApiResponse(responseCode = "400", description = "인증번호 전송 실패",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = "{\n\"message\": \"Failed to send verification code\"\n}")))
    })
    public ResponseEntity<Map<String, String>> getSmsCode(Authentication authentication,
                                                          @RequestParam("phone") String phone){
        return userService.getSmsCode(phone);
    }

    @PostMapping("/phone")
    @Operation(summary="번호 저장하기")
    @ApiResponse(responseCode = "200", description = "성공!")
    public ResponseEntity<User> saveUserPhone(Authentication authentication,
                                              @RequestParam("phone") String phone) throws Exception {
        return userService.saveUserPhone(phone, getUserNameByAuthentication(authentication));
    }

}
