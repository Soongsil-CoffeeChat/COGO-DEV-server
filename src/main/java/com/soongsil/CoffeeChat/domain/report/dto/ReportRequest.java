package com.soongsil.CoffeeChat.domain.report.dto;

import org.jetbrains.annotations.NotNull;

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

        @NotNull private Long reporterId;

        @NotNull private Long reportedUserId;

        @NotNull private ReportReason reason;

        @NotNull private String additionalDetails;
    }
}
