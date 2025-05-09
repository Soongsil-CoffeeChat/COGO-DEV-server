package com.soongsil.CoffeeChat.domain.application.controller;

import static com.soongsil.CoffeeChat.global.uri.RequestUri.APPLICATION_URI;

import java.net.URI;
import java.util.List;

import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.soongsil.CoffeeChat.domain.application.dto.ApplicationRequest.ApplicationCreateRequest;
import com.soongsil.CoffeeChat.domain.application.dto.ApplicationResponse.ApplicationCreateResponse;
import com.soongsil.CoffeeChat.domain.application.dto.ApplicationResponse.ApplicationGetResponse;
import com.soongsil.CoffeeChat.domain.application.dto.ApplicationResponse.ApplicationMatchResponse;
import com.soongsil.CoffeeChat.domain.application.service.ApplicationService;
import com.soongsil.CoffeeChat.global.api.ApiResponse;
import com.soongsil.CoffeeChat.global.security.oauth2.CustomOAuth2User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(APPLICATION_URI)
@RequiredArgsConstructor
@Tag(name = "APPLICATION", description = "Application 관련 api")
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping
    @Operation(summary = "COGO 신청하기")
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

    @GetMapping("/status")
    @Operation(summary = "신청 받은 COGO 조회 (MATCHED/UNMATCHED)")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "조건에 맞는 COGO LIST 반환")
    public ResponseEntity<ApiResponse<List<ApplicationGetResponse>>> getApplications(
            Authentication authentication, @RequestParam("status") String applicationStatus) {
        return ResponseEntity.ok()
                .body(
                        ApiResponse.onSuccessOK(
                                applicationService.getApplications(
                                        ((CustomOAuth2User) authentication.getPrincipal())
                                                .getUsername(),
                                        applicationStatus)));
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
