package com.soongsil.CoffeeChat.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.soongsil.CoffeeChat.entity.Application;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ApplicationGetResponseDto {

    @JsonProperty("application_id")
    private Long applicationId;

    @JsonProperty("mentee_name")
    private String menteeName;

    @JsonProperty("mentor_name")
    private String mentorName;

    @JsonProperty("application_memo")
    private String memo;

    @JsonProperty("application_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @JsonProperty("application_start_time")
    @Schema(type = "string", pattern = "hh:mm:ss")
    private LocalTime startTime;

    @JsonProperty("application_end_time")
    @Schema(type = "string", pattern = "hh:mm:ss")
    private LocalTime endTime;

    public static ApplicationGetResponseDto toDto(
            Application application, String mentorName, String menteeName) {
        return ApplicationGetResponseDto.builder()
                .applicationId(application.getId())
                .mentorName(mentorName)
                .menteeName(menteeName)
                .memo(application.getMemo())
                .date(application.getPossibleDate().getDate())
                .startTime(application.getPossibleDate().getStartTime())
                .endTime(application.getPossibleDate().getEndTime())
                .build();
    }
}
