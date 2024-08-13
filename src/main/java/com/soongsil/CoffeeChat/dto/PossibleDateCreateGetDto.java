package com.soongsil.CoffeeChat.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.querydsl.core.annotations.QueryProjection;
import com.soongsil.CoffeeChat.entity.PossibleDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PossibleDateCreateGetDto {
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate date;

	@JsonFormat(pattern = "HH:mm")
	private LocalTime startTime;

	@JsonFormat(pattern = "HH:mm")
	private LocalTime endTime;

	@QueryProjection
	public PossibleDateCreateGetDto(LocalDate date, LocalTime startTime, LocalTime endTime, Long possibleDateId) {
		this.date = date;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	public static PossibleDateCreateGetDto toDto(PossibleDate possibleDate) {
		return PossibleDateCreateGetDto.builder()
			.date(possibleDate.getDate())
			.startTime(possibleDate.getStartTime())
			.endTime(possibleDate.getEndTime())
			.build();
	}
}
