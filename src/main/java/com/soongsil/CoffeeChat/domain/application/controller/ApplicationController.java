package com.soongsil.CoffeeChat.domain.application.controller;

import static com.soongsil.CoffeeChat.global.uri.RequestUri.APPLICATION_URI;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.soongsil.CoffeeChat.domain.application.dto.ApplicationRequest.ApplicationCreateRequest;
import com.soongsil.CoffeeChat.domain.application.dto.ApplicationResponse.ApplicationCreateResponse;
import com.soongsil.CoffeeChat.domain.application.dto.ApplicationResponse.ApplicationGetResponse;
import com.soongsil.CoffeeChat.domain.application.dto.ApplicationResponse.ApplicationMatchResponse;
import com.soongsil.CoffeeChat.domain.application.dto.ApplicationSummaryResponse;
import com.soongsil.CoffeeChat.domain.application.enums.ApplicationStatus;
import com.soongsil.CoffeeChat.domain.application.service.ApplicationService;
import com.soongsil.CoffeeChat.global.api.ApiResponse;
import com.soongsil.CoffeeChat.global.security.oauth2.CustomOAuth2User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(APPLICATION_URI)
@RequiredArgsConstructor
@Tag(name = "APPLICATION", description = "Application 관련 api")
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping
    @Operation(summary = "COGO 신청하기 + 테스트용 출력")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "COGO 기본 정보 반환")
    public ResponseEntity<ApiResponse<ApplicationCreateResponse>> createApplication(
            Authentication authentication, @RequestBody ApplicationCreateRequest request) {
        ApplicationCreateResponse applicationDto =
                applicationService.createApplication(
                        request, ((CustomOAuth2User) authentication.getPrincipal()).getUsername());
        return ResponseEntity.created(
                        URI.create(APPLICATION_URI + "/" + applicationDto.getApplicationId()))
                .body(ApiResponse.onSuccessCREATED(applicationDto));
    }

    @GetMapping("/{applicationId}")
    @Operation(summary = "특정 COGO 조회")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "COGO 세부 정보 반환")
    public ResponseEntity<ApiResponse<ApplicationGetResponse>> getApplication(
            @PathVariable Long applicationId) {
        return ResponseEntity.ok()
                .body(ApiResponse.onSuccessOK(applicationService.getApplication(applicationId)));
    }

    @GetMapping("/list")
    @Operation(
            summary = "신청 받은 COGO 조회 ",
            description = "해당 사용자가 관여한 코고 시넝서를 status (unmatched/matched/rejected)로 필터링")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Application 상대 이름, 날짜, 상태 반환")
    public ResponseEntity<ApiResponse<List<ApplicationSummaryResponse>>> getApplications(
            Authentication authentication,
            @Parameter(name = "status", description = "unmatched | matched | rejected | 공란 시 전체 조회")
                    @RequestParam(required = false)
                    ApplicationStatus status) {

        String userName = authentication.getName();
        log.info(">> 로그 출력: auth.name={}", authentication.getName());

        List<ApplicationSummaryResponse> responses =
                applicationService.getApplications(userName, status);

        return ResponseEntity.ok(ApiResponse.onSuccessOK(responses));
    }

    @PatchMapping("/{applicationId}/decision")
    @Operation(summary = "신청 받은 COGO 수락 / 거절")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "수락 / 거절한 COGO 정보 반환")
    public ResponseEntity<ApiResponse<ApplicationMatchResponse>> updateApplicationStatus(
            @PathVariable Long applicationId, @RequestParam("decision") String decision) {
        return ResponseEntity.ok()
                .body(
                        ApiResponse.onSuccessOK(
                                applicationService.updateApplicationStatus(
                                        applicationId, decision)));
    }
}
