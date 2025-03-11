package com.soongsil.CoffeeChat.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.soongsil.CoffeeChat.entity.Application;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ApplicationCreateResponseDto {

    @JsonProperty("application_id")
    private Long applicationId;

    @JsonProperty("mentor_id")
    private Long mentorId;

    @JsonProperty("mentee_id")
    private Long menteeId;

    @JsonProperty("application_memo")
    private String applicationMemo;

    @JsonProperty("application_date")
    @Schema(type = "string", pattern = "yyyy-mm-dd")
    private LocalDate applicationDate;

    @JsonProperty("application_start_time")
    @Schema(type = "string", pattern = "hh:mm:ss")
    private LocalTime applicationStartTime;

    @JsonProperty("application_end_time")
    @Schema(type = "string", pattern = "hh:mm:ss")
    private LocalTime applicationEndTime;

    public static ApplicationCreateResponseDto from(Application application) {
        return ApplicationCreateResponseDto.builder()
                .applicationId(application.getId())
                .mentorId(application.getMentor().getId())
                .menteeId(application.getMentee().getId())
                .applicationMemo(application.getMemo())
                .applicationDate(application.getPossibleDate().getDate())
                .applicationStartTime(application.getPossibleDate().getStartTime())
                .applicationEndTime(application.getPossibleDate().getEndTime())
                .build();
    }
}
