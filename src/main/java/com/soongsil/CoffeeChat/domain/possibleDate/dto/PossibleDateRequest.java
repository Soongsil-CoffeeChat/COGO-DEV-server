package com.soongsil.CoffeeChat.domain.possibleDate.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

public class PossibleDateRequest {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PossibleDateCreateRequest {
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate date;

        @JsonFormat(pattern = "HH:mm")
        @Schema(type = "string", pattern = "HH:mm")
        private LocalTime startTime;

        @JsonFormat(pattern = "HH:mm")
        @Schema(type = "string", pattern = "HH:mm")
        private LocalTime endTime;
    }
}
