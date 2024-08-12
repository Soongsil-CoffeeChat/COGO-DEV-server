package com.soongsil.CoffeeChat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
public class ApplicationGetResponse {
	private Long mentorId;
	private Long menteeId;
	private String memo;
	private Long possibleDateId;
}
