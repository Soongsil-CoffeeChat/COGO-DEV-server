package com.soongsil.CoffeeChat.domain.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

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
        private String menteeName;
        private String mentorName;
        private String applicationMemo;

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
    public static class ApplicationMatchResponse {
        private Long applicationId;
        private String applicationStatus;
    }
}
