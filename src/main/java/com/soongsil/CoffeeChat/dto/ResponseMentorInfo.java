package com.soongsil.CoffeeChat.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.soongsil.CoffeeChat.entity.Mentor;
import com.soongsil.CoffeeChat.entity.User;

import com.soongsil.CoffeeChat.enums.PartEnum;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
@Setter
@Data
public class ResponseMentorInfo {
	Long mentorId;
	String mentorName;
	PartEnum part;
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
			.part(mentor.getPart())
			.introductionTitle(mentor.getIntroduction().getTitle())
			.introductionDescription(mentor.getIntroduction().getDescription())
			.introductionAnswer1(mentor.getIntroduction().getAnswer1())
			.introductionAnswer2(mentor.getIntroduction().getAnswer2())
			.build();
	}

	@QueryProjection
	public ResponseMentorInfo(Long mentorId, String mentorName, PartEnum part, String introductionTitle, String introductionDescription,
							  String introductionAnswer1, String introductionAnswer2, String imageUrl){
		this.mentorId=mentorId;
		this.mentorName=mentorName;
		this.part=part;
		this.introductionTitle=introductionTitle;
		this.introductionDescription=introductionDescription;
		this.introductionAnswer1=introductionAnswer1;
		this.introductionAnswer2=introductionAnswer2;
		this.imageUrl=imageUrl;
	}
}
