package com.soongsil.CoffeeChat.domain.possibleDate.controller;

import static com.soongsil.CoffeeChat.global.uri.RequestUri.POSSIBLEDATE_URI;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.soongsil.CoffeeChat.domain.possibleDate.dto.PossibleDateResponse.PossibleDateDetailResponse;
import com.soongsil.CoffeeChat.domain.possibleDate.service.PossibleDateService;
import com.soongsil.CoffeeChat.global.api.ApiResponse;
import com.soongsil.CoffeeChat.global.security.oauth2.CustomOAuth2User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(POSSIBLEDATE_URI)
@RequiredArgsConstructor
@Tag(name = "POSSIBLEDATE", description = "커피챗 시간 관련 api")
public class PossibleDateController {
    private final PossibleDateService possibleDateService;

    private String getUserNameByAuthentication(Authentication authentication) throws Exception {
        CustomOAuth2User principal = (CustomOAuth2User) authentication.getPrincipal();
        if (principal == null) throw new Exception(); // TODO : Exception 만들기
        return principal.getUsername();
    }

    @GetMapping("{mentorId}")
    @Operation(summary = "멘토ID로 커피챗 가능시간 불러오기")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "DTO LIST형식으로 정보 반환")
    public ResponseEntity<ApiResponse<List<PossibleDateDetailResponse>>> getPossibleDates(
            @PathVariable("mentorId") Long mentorId) {
        return ResponseEntity.ok()
                .body(
                        ApiResponse.onSuccessOK(
                                possibleDateService.findPossibleDateListByMentor(mentorId)));
    }

    @GetMapping("")
    @Operation(summary = "토큰으로 멘토 본인의 커피챗 가능시간 불러오기")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "DTO LIST형식으로 정보 반환")
    public ResponseEntity<ApiResponse<List<PossibleDateDetailResponse>>> getPossibleDatesByToken(
            Authentication authentication) throws Exception {
        return ResponseEntity.ok()
                .body(
                        ApiResponse.onSuccessOK(
                                possibleDateService.findMentorPossibleDateListByUsername(
                                        getUserNameByAuthentication(authentication))));
    }

    //PostMappping
    //PatchMapping
}
