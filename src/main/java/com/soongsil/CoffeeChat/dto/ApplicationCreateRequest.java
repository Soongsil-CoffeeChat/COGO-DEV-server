package com.soongsil.CoffeeChat.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.soongsil.CoffeeChat.entity.Application;
import com.soongsil.CoffeeChat.entity.Mentee;
import com.soongsil.CoffeeChat.entity.Mentor;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ApplicationCreateRequest {

	private Long mentorId;
	private Long menteeId;
	private Long possibleDateId;
	private String memo;

	public Application toEntity(Mentor mentor, Mentee mentee, String memo) {
		return Application.builder()
			.mentor(mentor)
			.mentee(mentee)
			.memo(memo)
			.build();
	}
}