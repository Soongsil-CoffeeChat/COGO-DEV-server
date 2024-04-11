package com.soongsil.CoffeeChat.controller;

import static com.soongsil.CoffeeChat.enums.RequestUri.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.soongsil.CoffeeChat.dto.CreateMenteeRequest;
import com.soongsil.CoffeeChat.dto.CreateMentorRequest;
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

@RestController
@RequestMapping(USER_URI)
@RequiredArgsConstructor
@Tag(name="USER", description = "유저 관련 api")
public class UserController {
    private final UserService userService;
    @PostMapping("/join/mentor")
    @Operation(summary="멘토로 가입하기!")
    @ApiResponse(responseCode = "200", description = "성공!")
    public ResponseEntity<Mentor> joinWithMentor(@AuthenticationPrincipal User user,
                                                 @RequestBody CreateMentorRequest dto){
        System.out.println("user.getName() = " + user.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(
                userService.saveMentorInformation(user, dto)
        );
    }

    @PostMapping("/join/mentee")
    @Operation(summary="멘티로 가입하기!")
    @ApiResponse(responseCode = "200", description = "성공!")
    public ResponseEntity<Mentee> joinWithMentee(Authentication authentication,
                                                 @RequestBody CreateMenteeRequest dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(
                userService.saveMenteeInformation(authentication.getName(), dto)
        );
    }

}
