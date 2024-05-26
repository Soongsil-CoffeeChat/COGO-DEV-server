package com.soongsil.CoffeeChat.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.soongsil.CoffeeChat.entity.PossibleDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PossibleDateRequestDto {
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate date;

	@JsonFormat(pattern = "HH:mm")
	private LocalTime startTime;

	@JsonFormat(pattern = "HH:mm")
	private LocalTime endTime;

	public static PossibleDateRequestDto toDto(PossibleDate possibleDate) {
		return PossibleDateRequestDto.builder()
			.date(possibleDate.getDate())
			.startTime(possibleDate.getStartTime())
			.endTime(possibleDate.getEndTime())
			.build();
	}
}
