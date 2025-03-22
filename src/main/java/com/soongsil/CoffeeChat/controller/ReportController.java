package com.soongsil.CoffeeChat.controller;

import static com.soongsil.CoffeeChat.global.uri.RequestUri.REPORT_URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.soongsil.CoffeeChat.dto.ReportDto;
import com.soongsil.CoffeeChat.global.security.oauth2.CustomOAuth2User;
import com.soongsil.CoffeeChat.service.ReportService;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(REPORT_URI)
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    private String getUserNameByAuthentication(Authentication authentication) throws Exception {
        CustomOAuth2User principal = (CustomOAuth2User) authentication.getPrincipal();
        if (principal == null) throw new Exception(); // TODO : Exception 만들기
        return principal.getUsername();
    }

    @PostMapping("/mentor")
    @ApiResponse(responseCode = "201", description = "성공!")
    public ResponseEntity<?> createReportMentor(
            Authentication authentication, @RequestBody ReportDto request) throws Exception {
        ReportDto response =
                reportService.createReportMentor(
                        request, getUserNameByAuthentication(authentication));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/mentee")
    @ApiResponse(responseCode = "201", description = "성공!")
    public ResponseEntity<?> createReportMentee(
            Authentication authentication, @RequestBody ReportDto request) throws Exception {
        ReportDto response =
                reportService.createReportMentee(
                        request, getUserNameByAuthentication(authentication));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
