package com.soongsil.CoffeeChat.domain.report.controller;

import static com.soongsil.CoffeeChat.global.uri.RequestUri.REPORT_URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.soongsil.CoffeeChat.domain.report.dto.ReportDto;
import com.soongsil.CoffeeChat.domain.report.service.ReportService;
import com.soongsil.CoffeeChat.global.annotation.CurrentUsername;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(REPORT_URI)
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @PostMapping("/mentor")
    @ApiResponse(responseCode = "201", description = "성공!")
    public ResponseEntity<?> createReportMentor(
            @RequestBody ReportDto request,
            @Parameter(hidden = true) @CurrentUsername String username) {
        ReportDto response = reportService.createReportMentor(request, username);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/mentee")
    @ApiResponse(responseCode = "201", description = "성공!")
    public ResponseEntity<?> createReportMentee(
            @RequestBody ReportDto request,
            @Parameter(hidden = true) @CurrentUsername String username) {
        ReportDto response = reportService.createReportMentee(request, username);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
