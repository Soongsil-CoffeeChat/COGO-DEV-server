package com.soongsil.CoffeeChat.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.querydsl.core.annotations.QueryProjection;
import com.soongsil.CoffeeChat.entity.PossibleDate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@Data
public class PossibleDateCreateRequestDto {

	@JsonProperty("date")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate date;

	@JsonProperty("start_time")
	@Schema(type = "string", pattern = "HH-mm")
	private LocalTime startTime;

	@JsonProperty("end_time")
	@Schema(type = "string", pattern = "HH-mm")
	private LocalTime endTime;

	@QueryProjection
	public PossibleDateCreateRequestDto(LocalDate date, LocalTime startTime, LocalTime endTime) {
		this.date = date;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	public static PossibleDateCreateRequestDto toDto(PossibleDate possibleDate) {
		return PossibleDateCreateRequestDto.builder()
			.date(possibleDate.getDate())
			.startTime(possibleDate.getStartTime())
			.endTime(possibleDate.getEndTime())
			.build();
	}
}
