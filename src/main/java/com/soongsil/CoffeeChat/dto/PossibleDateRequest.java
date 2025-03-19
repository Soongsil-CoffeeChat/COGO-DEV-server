package com.soongsil.CoffeeChat.dto;

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

        @Schema(type = "string", pattern = "hh:mm:ss")
        private LocalTime startTime;

        @Schema(type = "string", pattern = "hh:mm:ss")
        private LocalTime endTime;
    }
}
