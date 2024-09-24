package com.soongsil.CoffeeChat.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.querydsl.core.annotations.QueryProjection;
import com.soongsil.CoffeeChat.entity.PossibleDate;

import io.swagger.v3.oas.annotations.media.Schema;
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
public class PossibleDateCreateGetResponseDto {
	@JsonProperty("possible_date_id")
	private Long possibledateId;

	@JsonProperty("date")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate date;

	@JsonProperty("start_time")
	@Schema(type = "string", pattern = "HH-mm")
	private LocalTime startTime;

	@JsonProperty("end_time")
	@Schema(type = "string", pattern = "HH-mm")
	private LocalTime endTime;

	@JsonIgnore

	@QueryProjection
	public PossibleDateCreateGetResponseDto(LocalDate date, LocalTime startTime, LocalTime endTime,
		Long possibledateId) {
		this.date = date;
		this.startTime = startTime;
		this.endTime = endTime;
		this.possibledateId = possibledateId;
	}

	public static PossibleDateCreateGetResponseDto from(PossibleDate possibleDate) {
		return PossibleDateCreateGetResponseDto.builder()
			.date(possibleDate.getDate())
			.startTime(possibleDate.getStartTime())
			.endTime(possibleDate.getEndTime())
			.possibledateId(possibleDate.getId())
			.build();
	}
}
