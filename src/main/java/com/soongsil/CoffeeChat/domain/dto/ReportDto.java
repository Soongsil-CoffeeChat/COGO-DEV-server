package com.soongsil.CoffeeChat.domain.dto;

import com.soongsil.CoffeeChat.domain.entity.Report;
import com.soongsil.CoffeeChat.domain.entity.enums.ReportReason;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ReportDto {
    // private Long reporterId;
    private Long reportedUserId;
    private ReportReason reason;
    private String additionalDetails;

    public static ReportDto from(Report report) {
        return ReportDto.builder()
                .reportedUserId(report.getReportedUserId())
                .reason(report.getReason())
                .additionalDetails(report.getAdditionalDetails())
                .build();
    }
}
