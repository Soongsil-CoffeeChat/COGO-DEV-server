package com.soongsil.CoffeeChat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@Getter
public class MentorUpdateRequestDto {

	@JsonProperty("mentor_name")
	private String mentorName;
	@JsonProperty("mentor_phone_number")
	private String mentorPhoneNumber;
	@JsonProperty("mentor_email")
	private String mentorEmail;
}
