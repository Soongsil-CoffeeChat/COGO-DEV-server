package com.soongsil.CoffeeChat.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.soongsil.CoffeeChat.entity.Application;
import com.soongsil.CoffeeChat.entity.Mentee;
import com.soongsil.CoffeeChat.entity.Mentor;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ApplicationCreateRequest {
    @JsonProperty("date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @JsonProperty("start_time")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;

    @JsonProperty("end_time")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;

    @JsonProperty("mentor_id")
    private Long mentorId;

    public ApplicationCreateRequest(
            LocalDate date, LocalTime startTime, LocalTime endTime, Long mentorId) {
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.mentorId = mentorId;
    }

    public Application toEntity(Mentor mentor, Mentee mentee) {
        return Application.builder().mentor(mentor).mentee(mentee).build();
    }
}
