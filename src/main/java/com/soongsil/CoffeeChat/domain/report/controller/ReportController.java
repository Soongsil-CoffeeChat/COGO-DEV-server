package com.soongsil.CoffeeChat.domain.report.controller;

import static com.soongsil.CoffeeChat.global.uri.RequestUri.REPORT_URI;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.soongsil.CoffeeChat.domain.report.dto.ReportRequest;
import com.soongsil.CoffeeChat.domain.report.dto.ReportResponse;
import com.soongsil.CoffeeChat.domain.report.service.ReportService;
import com.soongsil.CoffeeChat.global.api.ApiResponse;
import com.soongsil.CoffeeChat.global.security.oauth2.CustomOAuth2User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(REPORT_URI)
@RequiredArgsConstructor
@Tag(name = "REPORT", description = "report(신고) 관련 api")
public class ReportController {
    private final ReportService reportService;

    @PostMapping
    @Operation(
            summary = "사용자 리포트(신고) 등록",
            description =
                    """
                     - default : status=PENDING, reportedAt= 신고 요청 시간
                     - reason 값:
                        - NON_MENTORING_PURPOSE, DISPUTE, FALSE_INFORMATION, ABUSIVE_LANGUAGE, OTHER
                     - reason 이 OTHER 인 경우, otherReason 필수 입력
                     """)
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Report의 세부 정보 반환")
    public ResponseEntity<ApiResponse<ReportResponse.ReportCreateResponse>> createReport(
            Authentication authentication,
            @Valid @RequestBody ReportRequest.ReportCreateRequest request) {
        ReportResponse.ReportCreateResponse response =
                reportService.createReport(
                        ((CustomOAuth2User) authentication.getPrincipal()).getUsername(), request);
        return ResponseEntity.ok(ApiResponse.onSuccessCREATED(response));
    }
}
