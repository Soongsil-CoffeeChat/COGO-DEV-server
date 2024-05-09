package com.soongsil.CoffeeChat.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.soongsil.CoffeeChat.entity.Application;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ApplicationCreateResponse {
	private LocalDate date;
	private LocalTime startTime;
	private LocalTime endTime;

	public static ApplicationCreateResponse from(Application application) {
		return ApplicationCreateResponse.builder()
			.date(application.getDate())
			.startTime(application.getStartTime())
			.endTime(application.getEndTime())
			.build();
	}
}
