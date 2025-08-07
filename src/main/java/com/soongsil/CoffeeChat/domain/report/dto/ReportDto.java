package com.soongsil.CoffeeChat.domain.report.dto;

import com.soongsil.CoffeeChat.domain.report.entity.Report;
import com.soongsil.CoffeeChat.domain.report.enums.ReportReason;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ReportDto {
    // private Long reporterId;
    @NotNull
    @Schema(description = "신고 대상 ID", example = "12")
    private Long reportedUserId;

    @NotNull
    @Schema(description = "신고 사유 코드", implementation = ReportReason.class)
    private ReportReason reason;

    @Schema(description = "신고 세부 내용", example = "OOO님이 약속을 지키지 않았습니다.")
    private String additionalDetails;

    public static ReportDto from(Report report) {
        return ReportDto.builder()
                .reportedUserId(report.getReportedUserId())
                .reason(report.getReason())
                .additionalDetails(report.getAdditionalDetails())
                .build();
    }
}
