package com.soongsil.CoffeeChat.domain.report.dto;

import org.jetbrains.annotations.NotNull;

import com.soongsil.CoffeeChat.domain.report.enums.ReportReason;
import com.soongsil.CoffeeChat.global.exception.GlobalErrorCode;
import com.soongsil.CoffeeChat.global.exception.GlobalException;

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

        @NotNull private Long reporterId;

        @NotNull private Long reportedUserId;

        @NotNull private ReportReason reason;

        private String otherReason;
        private String additionalDetails;

        // ReportReason 타입이 OTHER 일 시, otherReason 필수 입력 검증
        public void validateReason() {
            if (reason == ReportReason.OTHER) {
                if (otherReason == null || otherReason.isBlank())
                    throw new GlobalException(GlobalErrorCode.BAD_REQUEST);
            }
        }
    }
}
