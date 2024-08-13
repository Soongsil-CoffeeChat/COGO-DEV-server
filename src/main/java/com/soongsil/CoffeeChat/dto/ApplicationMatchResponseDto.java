package com.soongsil.CoffeeChat.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ApplicationMatchResponseDto {

	@JsonProperty("application_id")
	private Long applicationId;

	@JsonProperty("application_status")
	private String status;
}
