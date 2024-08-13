package com.soongsil.CoffeeChat.dto;

import com.soongsil.CoffeeChat.entity.Application;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ApplicationGetResponseDto {
	private Long mentorId;
	private Long menteeId;
	private String memo;
	private Long possibleDateId;

	public static ApplicationGetResponseDto toDto(Application application){
		return ApplicationGetResponseDto.builder()
				.mentorId(application.getMentor().getId())
				.menteeId(application.getMentee().getId())
				.memo(application.getMemo())
				.possibleDateId(application.getPossibleDate().getId())
				.build();
	}
}
