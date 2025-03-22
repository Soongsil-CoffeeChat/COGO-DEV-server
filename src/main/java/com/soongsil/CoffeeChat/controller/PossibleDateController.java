package com.soongsil.CoffeeChat.controller;

import static com.soongsil.CoffeeChat.global.uri.RequestUri.*;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.soongsil.CoffeeChat.dto.PossibleDateRequest.*;
import com.soongsil.CoffeeChat.dto.PossibleDateResponse;
import com.soongsil.CoffeeChat.global.api.ApiResponseGenerator;
import com.soongsil.CoffeeChat.global.security.oauth2.CustomOAuth2User;
import com.soongsil.CoffeeChat.service.PossibleDateService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

    @PostMapping()
    @Operation(summary = "멘토가 직접 커피챗 가능시간 갱신하기")
    @ApiResponse(responseCode = "201", description = "DTO형식으로 정보 반환")
    public ResponseEntity<
                    ApiResponseGenerator<List<PossibleDateResponse.PossibleDateCreateResponse>>>
            addPossibleDate(
                    Authentication authentication,
                    @RequestBody List<PossibleDateCreateRequest> dtos)
                    throws Exception {
        return ResponseEntity.created(URI.create(POSSIBLEDATE_URI))
                .body(
                        ApiResponseGenerator.onSuccessCREATED(
                                possibleDateService.updatePossibleDate(
                                        dtos, getUserNameByAuthentication(authentication))));
    }

    @GetMapping("{mentorId}")
    @Operation(summary = "멘토ID로 커피챗 가능시간 불러오기")
    @ApiResponse(responseCode = "200", description = "DTO LIST형식으로 정보 반환")
    public ResponseEntity<
                    ApiResponseGenerator<List<PossibleDateResponse.PossibleDateCreateResponse>>>
            getPossibleDates(
                    Authentication authentication, @PathVariable("mentorId") Long mentorId) {
        return ResponseEntity.ok()
                .body(
                        ApiResponseGenerator.onSuccessOK(
                                possibleDateService.findPossibleDateListByMentor(mentorId)));
    }

    @GetMapping("")
    @Operation(summary = "토큰으로 멘토 본인의 커피챗 가능시간 불러오기")
    @ApiResponse(responseCode = "200", description = "DTO LIST형식으로 정보 반환")
    public ResponseEntity<
                    ApiResponseGenerator<List<PossibleDateResponse.PossibleDateCreateResponse>>>
            getPossibleDatesByToken(Authentication authentication) throws Exception {
        return ResponseEntity.ok()
                .body(
                        ApiResponseGenerator.onSuccessOK(
                                possibleDateService.findMentorPossibleDateListByUsername(
                                        getUserNameByAuthentication(authentication))));
    }

    @DeleteMapping("{possibleDateId}")
    @Operation(summary = "시간대ID로 등록한 시간대 삭제")
    @ApiResponse(responseCode = "200", description = "삭제 성공")
    public ResponseEntity<ApiResponseGenerator<String>> deletePossibleDates(
            Authentication authentication, @PathVariable("possibleDateId") Long possibleDateId)
            throws Exception {
        return ResponseEntity.ok()
                .body(
                        ApiResponseGenerator.onSuccessOK(
                                possibleDateService.deletePossibleDate(
                                        possibleDateId,
                                        getUserNameByAuthentication(authentication))));
    }
}
