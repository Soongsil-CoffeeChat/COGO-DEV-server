package com.soongsil.CoffeeChat.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.querydsl.core.annotations.QueryProjection;
import com.soongsil.CoffeeChat.entity.PossibleDate;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@Data
public class PossibleDateRequestDto {
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate date;

	@JsonFormat(pattern = "HH:mm")
	private LocalTime startTime;

	@JsonFormat(pattern = "HH:mm")
	private LocalTime endTime;

	private Long possibleDateId;

	@QueryProjection
	public PossibleDateRequestDto(LocalDate date, LocalTime startTime, LocalTime endTime, Long possibleDateId) {
		this.date = date;
		this.startTime = startTime;
		this.endTime = endTime;
		this.possibleDateId=possibleDateId;
	}

	public static PossibleDateRequestDto toDto(PossibleDate possibleDate) {
		return PossibleDateRequestDto.builder()
			.date(possibleDate.getDate())
			.startTime(possibleDate.getStartTime())
			.endTime(possibleDate.getEndTime())
			.build();
	}
}
