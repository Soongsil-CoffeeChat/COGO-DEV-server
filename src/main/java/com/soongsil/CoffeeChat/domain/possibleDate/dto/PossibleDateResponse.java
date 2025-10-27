package com.soongsil.CoffeeChat.domain.possibleDate.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

public class PossibleDateResponse {
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class PossibleDateDetailResponse {
        private Long possibleDateId;

        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate date;

        @JsonFormat(pattern = "HH:mm")
        @Schema(type = "string", pattern = "hh:mm")
        private LocalTime startTime;

        @JsonFormat(pattern = "HH:mm")
        @Schema(type = "string", pattern = "hh:mm")
        private LocalTime endTime;

        private boolean isActive;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class PossibleDateCreateUpdateResponse {
        private Long possibleDateId;

        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate date;

        @JsonFormat(pattern = "HH:mm")
        @Schema(type = "string", pattern = "hh:mm")
        private LocalTime startTime;

        @JsonFormat(pattern = "HH:mm")
        @Schema(type = "string", pattern = "hh:mm")
        private LocalTime endTime;

        private boolean isActive;
    }

}
