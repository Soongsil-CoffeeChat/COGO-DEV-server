package com.soongsil.CoffeeChat.domain.report.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.soongsil.CoffeeChat.domain.report.enums.ReportReason;
import com.soongsil.CoffeeChat.domain.report.enums.ReportStatus;

import lombok.*;

public class ReportResponse {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ReportCreateResponse {
        private Long reportId;
        private Long reporterId;
        private Long reportedUserId;
        private ReportReason reason;
        private String otherReason;
        private String additionalDetails;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
        private LocalDateTime reportedAt;

        private ReportStatus status;
    }
}
