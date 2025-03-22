package com.soongsil.CoffeeChat.controller;

import static com.soongsil.CoffeeChat.global.uri.RequestUri.MENTOR_URI;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.soongsil.CoffeeChat.dto.MentorRequest.*;
import com.soongsil.CoffeeChat.dto.MentorResponse.*;
import com.soongsil.CoffeeChat.enums.ClubEnum;
import com.soongsil.CoffeeChat.enums.PartEnum;
import com.soongsil.CoffeeChat.global.exception.handler.ApiResponseGenerator;
import com.soongsil.CoffeeChat.global.security.oauth2.CustomOAuth2User;
import com.soongsil.CoffeeChat.service.MentorService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RequestMapping(MENTOR_URI)
@RestController
@Tag(name = "MENTOR", description = "멘토 관련 api")
public class MentorController {

    private final MentorService mentorService;

    public MentorController(MentorService mentorService) {
        this.mentorService = mentorService;
    }

    private String getUserNameByAuthentication(Authentication authentication) throws Exception {
        CustomOAuth2User principal = (CustomOAuth2User) authentication.getPrincipal();
        if (principal == null) throw new Exception(); // TODO : Exception 만들기
        return principal.getUsername();
    }

    @PatchMapping
    @Operation(summary = "멘토의 세부 정보 수정")
    @ApiResponse(responseCode = "200", description = "변경된 멘토 세부 정보를 반환")
    public ResponseEntity<ApiResponseGenerator<MentorGetUpdateDetailResponse>> updateMentorInfo(
            Authentication authentication, @RequestBody MentorUpdateRequest request) {
        return ResponseEntity.ok()
                .body(
                        ApiResponseGenerator.onSuccessOK(
                                mentorService.updateMentorInfo(
                                        ((CustomOAuth2User) authentication.getPrincipal())
                                                .getUsername(),
                                        request)));
    }

    @GetMapping("/{mentorId}")
    @Operation(summary = "멘토 상세 정보 조회")
    @ApiResponse(responseCode = "200", description = "멘토 상세 정보 DTO 반환")
    public ResponseEntity<ApiResponseGenerator<MentorGetUpdateDetailResponse>> getMentorInfo(
            @PathVariable("mentorId") Long mentorId) {
        return ResponseEntity.ok()
                .body(
                        ApiResponseGenerator.onSuccessOK(
                                mentorService.getMentorDtoByIdWithJoin(mentorId)));
    }

    @PatchMapping("/introductions")
    @Operation(summary = "멘토 자기소개 입력")
    @ApiResponse(responseCode = "200", description = "자기소개의 수정된 버전을 반환")
    public ResponseEntity<ApiResponseGenerator<MentorIntroductionGetUpdateResponse>>
            updateMentoIntroduction(
                    Authentication authentication, @RequestBody MentorIntroductionUpdateRequest dto)
                    throws Exception {
        return ResponseEntity.ok()
                .body(
                        ApiResponseGenerator.onSuccessOK(
                                mentorService.updateMentorIntroduction(
                                        getUserNameByAuthentication(authentication), dto)));
    }

    @GetMapping("/introductions")
    @Operation(summary = "토큰으로 멘토 자기소개 항목 4개 조회")
    @ApiResponse(responseCode = "200", description = "토큰으로 멘토 본인의 자기소개 조회")
    public ResponseEntity<ApiResponseGenerator<MentorIntroductionGetUpdateResponse>>
            getMentorIntroduction(Authentication authentication) throws Exception {
        return ResponseEntity.ok()
                .body(
                        ApiResponseGenerator.onSuccessOK(
                                mentorService.getMentorIntroduction(
                                        getUserNameByAuthentication(authentication))));
    }

    @GetMapping("/part")
    @Operation(summary = "파트별 멘토 리스트 가져오기")
    @ApiResponse(responseCode = "200", description = "DTO LIST형식으로 정보 반환")
    public ResponseEntity<ApiResponseGenerator<List<MentorListResponse>>> getMentorListByPart(
            @RequestParam("part") PartEnum part) {
        return ResponseEntity.ok()
                .body(ApiResponseGenerator.onSuccessOK(mentorService.getMentorDtoListByPart(part)));
    }

    @GetMapping("/club")
    @Operation(summary = "동아리별 멘토 리스트 가져오기")
    @ApiResponse(responseCode = "200", description = "DTO LIST형식으로 정보 반환")
    public ResponseEntity<ApiResponseGenerator<List<MentorListResponse>>> getMentorListByClub(
            @RequestParam("club") ClubEnum club) {
        return ResponseEntity.ok()
                .body(ApiResponseGenerator.onSuccessOK(mentorService.getMentorDtoListByClub(club)));
    }

    @GetMapping("/part/club")
    @Operation(summary = "파트+동아리별 멘토 리스트 가져오기")
    @ApiResponse(responseCode = "200", description = "DTO LIST형식으로 정보 반환")
    public ResponseEntity<ApiResponseGenerator<List<MentorListResponse>>> getMentorListByClub(
            @RequestParam("part") PartEnum part, @RequestParam("club") ClubEnum club) {
        return ResponseEntity.ok()
                .body(
                        ApiResponseGenerator.onSuccessOK(
                                mentorService.getMentorDtoListByPartAndClub(part, club)));
    }
}
