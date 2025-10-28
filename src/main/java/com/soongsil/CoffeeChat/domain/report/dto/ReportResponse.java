package com.soongsil.CoffeeChat.domain.report.dto;

import com.soongsil.CoffeeChat.domain.report.enums.ReportReason;
import com.soongsil.CoffeeChat.domain.report.enums.ReportStatus;
import lombok.*;

import java.time.LocalDateTime;

public class ReportResponse {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ReportCreateResponse {
        private Long reportId;
        private Long reporterId;
        private Long reportedUserId;
        private ReportReason reportReason;
        private String additionalDetails;
        private LocalDateTime reportedAt;
        private ReportStatus status;
    }

}
