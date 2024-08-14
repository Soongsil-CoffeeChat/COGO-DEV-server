package com.soongsil.CoffeeChat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@Getter
public class MentorIntroductionUpdateRequestDto {

	@JsonProperty("introduction_title")
	private String title;

	@JsonProperty("introduction_description")
	private String description;

	@JsonProperty("introduction_answer1")
	private String answer1;

	@JsonProperty("introduction_answer2")
	private String answer2;
}
