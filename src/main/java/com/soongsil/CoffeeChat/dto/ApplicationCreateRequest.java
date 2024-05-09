package com.soongsil.CoffeeChat.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.soongsil.CoffeeChat.entity.Application;
import com.soongsil.CoffeeChat.entity.Mentee;
import com.soongsil.CoffeeChat.entity.Mentor;

import lombok.Getter;

@Getter
public class ApplicationCreateRequest {
	@JsonProperty("date")
	private LocalDate date;

	@JsonProperty("start_time")
	private LocalTime startTime;

	@JsonProperty("end_time")
	private LocalTime endTime;

	@JsonProperty("mentor_id")
	private Long mentorId;

	public Application toEntity(Mentor mentor, Mentee mentee) {
		return Application.builder()
			.date(this.date)
			.startTime(this.startTime)
			.endTime(this.endTime)
			.mentor(mentor)
			.mentee(mentee)
			.build();
	}
}