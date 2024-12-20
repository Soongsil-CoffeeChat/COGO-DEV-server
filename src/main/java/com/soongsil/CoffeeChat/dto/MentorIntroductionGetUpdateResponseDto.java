package com.soongsil.CoffeeChat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Builder
@Getter
public class MentorIntroductionGetUpdateResponseDto {

	@JsonProperty("introduction_title")
	@NonNull
	private String title;

	@JsonProperty("introduction_description")
	@NonNull
	private String description;

	@JsonProperty("introduction_answer1")
	@NonNull
	private String answer1;

	@JsonProperty("introduction_answer2")
	@NonNull
	private String answer2;
}
