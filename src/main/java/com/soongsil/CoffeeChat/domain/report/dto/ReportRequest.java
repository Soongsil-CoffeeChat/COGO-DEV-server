package com.soongsil.CoffeeChat.domain.report.dto;

import com.soongsil.CoffeeChat.domain.report.enums.ReportReason;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class ReportRequest {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReportCreateRequest {
        private Long reporterId;
        private Long reportedUserId;
        private ReportReason reason;
        private String additionalDetails;
    }
}
