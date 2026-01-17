package com.soongsil.CoffeeChat.domain.mentor.controller;

import static com.soongsil.CoffeeChat.global.uri.RequestUri.MENTOR_URI;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.soongsil.CoffeeChat.domain.mentor.dto.MentorRequest.MentorIntroductionUpdateRequest;
import com.soongsil.CoffeeChat.domain.mentor.dto.MentorRequest.MentorUpdateRequest;
import com.soongsil.CoffeeChat.domain.mentor.dto.MentorResponse.MentorDetailResponse;
import com.soongsil.CoffeeChat.domain.mentor.dto.MentorResponse.MentorIntroductionResponse;
import com.soongsil.CoffeeChat.domain.mentor.dto.MentorResponse.MentorListResponse;
import com.soongsil.CoffeeChat.domain.mentor.enums.ClubEnum;
import com.soongsil.CoffeeChat.domain.mentor.enums.PartEnum;
import com.soongsil.CoffeeChat.domain.mentor.service.MentorService;
import com.soongsil.CoffeeChat.global.api.ApiResponse;
import com.soongsil.CoffeeChat.global.security.oauth2.CustomOAuth2User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RequestMapping(MENTOR_URI)
@RestController
@RequiredArgsConstructor
@Tag(name = "MENTOR", description = "멘토 관련 api")
public class MentorController {

    private final MentorService mentorService;

    private String getUserNameByAuthentication(Authentication authentication) throws Exception {
        CustomOAuth2User principal = (CustomOAuth2User) authentication.getPrincipal();
        if (principal == null) throw new Exception(); // TODO : Exception 만들기
        return principal.getUsername();
    }

    @PatchMapping
    @Operation(summary = "멘토의 세부 정보 수정")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "변경된 멘토 세부 정보를 반환")
    public ResponseEntity<ApiResponse<MentorDetailResponse>> updateMentorInfo(
            Authentication authentication, @RequestBody MentorUpdateRequest request) {
        return ResponseEntity.ok()
                .body(
                        ApiResponse.onSuccessOK(
                                mentorService.updateMentorInfo(
                                        ((CustomOAuth2User) authentication.getPrincipal())
                                                .getUsername(),
                                        request)));
    }

    @GetMapping("/{mentorId}")
    @Operation(summary = "멘토 상세 정보 조회")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "멘토 상세 정보 DTO 반환")
    public ResponseEntity<ApiResponse<MentorDetailResponse>> getMentorInfo(
            Authentication authentication, @PathVariable("mentorId") Long mentorId)
            throws Exception {
        return ResponseEntity.ok()
                .body(
                        ApiResponse.onSuccessOK(
                                mentorService.getMentorDtoByIdWithJoin(
                                        getUserNameByAuthentication(authentication), mentorId)));
    }

    @PatchMapping("/introductions")
    @Operation(summary = "멘토 자기소개 입력")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "자기소개의 수정된 버전을 반환")
    public ResponseEntity<ApiResponse<MentorIntroductionResponse>> updateMentoIntroduction(
            Authentication authentication, @RequestBody MentorIntroductionUpdateRequest dto)
            throws Exception {
        return ResponseEntity.ok()
                .body(
                        ApiResponse.onSuccessOK(
                                mentorService.updateMentorIntroduction(
                                        getUserNameByAuthentication(authentication), dto)));
    }

    @GetMapping("/introductions")
    @Operation(summary = "토큰으로 멘토 자기소개 항목 4개 조회")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "토큰으로 멘토 본인의 자기소개 조회")
    public ResponseEntity<ApiResponse<MentorIntroductionResponse>> getMentorIntroduction(
            Authentication authentication) throws Exception {
        return ResponseEntity.ok()
                .body(
                        ApiResponse.onSuccessOK(
                                mentorService.getMentorIntroduction(
                                        getUserNameByAuthentication(authentication))));
    }

    @GetMapping("/list")
    @Operation(summary = "멘토 리스트 조회")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "DTO LIST형식으로 정보 반환")
    public ResponseEntity<ApiResponse<List<MentorListResponse>>> getMentorListByClub(
            Authentication authentication,
            @RequestParam(value = "part", required = false) PartEnum part,
            @RequestParam(value = "club", required = false) ClubEnum club)
            throws Exception {
        return ResponseEntity.ok()
                .body(
                        ApiResponse.onSuccessOK(
                                mentorService.getMentorList(
                                        getUserNameByAuthentication(authentication), part, club)));
    }
}
