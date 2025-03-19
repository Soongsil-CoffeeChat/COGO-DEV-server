package com.soongsil.CoffeeChat.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

public class ApplicationResponse {
    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ApplicationCreateResponse {
        private Long applicationId;
        private Long mentorId;
        private Long menteeId;
        private String applicationMemo;

        @Schema(type = "string", pattern = "yyyy-mm-dd")
        private LocalDate applicationDate;

        @Schema(type = "string", pattern = "hh:mm:ss")
        private LocalTime applicationStartTime;

        @Schema(type = "string", pattern = "hh:mm:ss")
        private LocalTime applicationEndTime;
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ApplicationGetResponse {
        private Long applicationId;
        private String menteeName;
        private String mentorName;
        private String applicationMemo;

        @Schema(type = "string", pattern = "yyyy-mm-dd")
        private LocalDate applicationDate;

        @Schema(type = "string", pattern = "hh:mm:ss")
        private LocalTime applicationStartTime;

        @Schema(type = "string", pattern = "hh:mm:ss")
        private LocalTime applicationEndTime;
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ApplicationMatchResponse {
        private Long applicationId;
        private String applicationStatus;
    }
}
