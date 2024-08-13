package com.soongsil.CoffeeChat.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.soongsil.CoffeeChat.entity.Application;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ApplicationCreateResponseDto {
	private LocalDate date;
	private LocalTime startTime;
	private LocalTime endTime;

	public static ApplicationCreateResponseDto from(Application application) {
		return ApplicationCreateResponseDto.builder()

			.build();
	}
}
