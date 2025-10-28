package com.soongsil.CoffeeChat.domain.report.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.soongsil.CoffeeChat.domain.report.enums.ReportReason;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ReportRequest {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReportCreateRequest {
        private Long reporterId;
        private Long reportedUserId;
        private ReportReason reason;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
        private String additionalDetails;
    }
}
