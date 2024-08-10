package com.soongsil.CoffeeChat.dto;

import com.soongsil.CoffeeChat.entity.Mentor;
import com.soongsil.CoffeeChat.entity.User;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResponseMentorInfo {
	Long mentorId;
	String mentorName;
	String part;
	String introductionTitle;
	String introductionDescription;
	String introductionAnswer1;
	String introductionAnswer2;
	String imageUrl;

	public static ResponseMentorInfo of(Mentor mentor, User user) {
		return ResponseMentorInfo.builder()
			.mentorId(mentor.getId())
			.mentorName(user.getName())
			.imageUrl(user.getPicture())
			.part(String.valueOf(mentor.getPart()))
			.introductionTitle(mentor.getIntroduction().getTitle())
			.introductionDescription(mentor.getIntroduction().getDescription())
			.introductionAnswer1(mentor.getIntroduction().getAnswer1())
			.introductionAnswer2(mentor.getIntroduction().getAnswer2())
			.build();
	}
}
