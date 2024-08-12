package com.soongsil.CoffeeChat.dto;

import com.soongsil.CoffeeChat.entity.Application;
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

	public static ApplicationGetResponse toDto(Application application){
		return ApplicationGetResponse.builder()
				.mentorId(application.getMentor().getId())
				.menteeId(application.getMentee().getId())
				.memo(application.getMemo())
				.possibleDateId(application.getPossibleDate().getId())
				.build();
	}
}
