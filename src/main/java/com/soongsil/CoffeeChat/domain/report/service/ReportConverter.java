package com.soongsil.CoffeeChat.domain.report.service;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import com.soongsil.CoffeeChat.domain.report.dto.ReportRequest.ReportCreateRequest;
import com.soongsil.CoffeeChat.domain.report.dto.ReportResponse.ReportCreateResponse;
import com.soongsil.CoffeeChat.domain.report.entity.Report;
import com.soongsil.CoffeeChat.domain.report.enums.ReportStatus;

public class ReportConverter {
    public static Report toEntity(ReportCreateRequest request) {
        return Report.builder()
                .reporterId(request.getReporterId())
                .reportedUserId(request.getReportedUserId())
                .reason(request.getReason())
                .additionalDetails(request.getAdditionalDetails())
                .reportedAt(ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toLocalDateTime())
                .status(ReportStatus.PENDING)
                .build();
    }

    public static ReportCreateResponse toResponse(Report report) {
        return ReportCreateResponse.builder()
                .reportId(report.getId())
                .reporterId(report.getReporterId())
                .reportedUserId(report.getReportedUserId())
                .reportReason(report.getReason())
                .additionalDetails(report.getAdditionalDetails())
                .reportedAt(report.getReportedAt())
                .status(report.getStatus())
                .build();
    }
}
