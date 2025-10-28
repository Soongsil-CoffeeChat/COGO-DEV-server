package com.soongsil.CoffeeChat.domain.application.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.soongsil.CoffeeChat.domain.application.enums.ApplicationRejectReason;
import com.soongsil.CoffeeChat.domain.application.enums.ApplicationStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

public class ApplicationResponse {
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ApplicationCreateResponse {
        private Long applicationId;
        private Long mentorId;
        private Long menteeId;
        private String applicationMemo;
        private ApplicationRejectReason rejectReason;

        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate applicationDate;

        @JsonFormat(pattern = "HH:mm")
        @Schema(type = "string", pattern = "HH:mm")
        private LocalTime applicationStartTime;

        @JsonFormat(pattern = "HH:mm")
        @Schema(type = "string", pattern = "HH:mm")
        private LocalTime applicationEndTime;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ApplicationGetResponse {
        private Long applicationId;
        private Long menteeId;
        private Long mentorId;
        private String applicationMemo;
        private ApplicationRejectReason applicationRejectReason;

        @JsonFormat(pattern = "yyyy-MM-dd")
        @Schema(type = "string", pattern = "yyyy-MM-dd")
        private LocalDate applicationDate;

        @JsonFormat(pattern = "HH:mm")
        @Schema(type = "string", pattern = "HH:mm")
        private LocalTime applicationStartTime;

        @JsonFormat(pattern = "HH:mm")
        @Schema(type = "string", pattern = "HH:mm")
        private LocalTime applicationEndTime;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ApplicationUpdateResponse {
        private Long applicationId;
        private ApplicationStatus applicationStatus;
        private ApplicationRejectReason reason;
    }
}
