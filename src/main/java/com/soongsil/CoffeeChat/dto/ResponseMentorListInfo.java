package com.soongsil.CoffeeChat.dto;

import java.util.ArrayList;
import java.util.List;

import com.querydsl.core.annotations.QueryProjection;
import com.soongsil.CoffeeChat.entity.Mentor;
import com.soongsil.CoffeeChat.entity.User;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@Data
public class ResponseMentorListInfo {
	private String picture;
	private String mentorName;
	private int part;
	private int club;
	private String username;
	private Long mentorId;
	private String title;
	private String description;


	@QueryProjection
	public ResponseMentorListInfo(String picture, String mentorName, int part, int club, String username,Long mentorId, String title, String description) {
		this.picture = picture;
		this.mentorName = mentorName;
		this.part=part;
		this.club=club;
		this.username = username;
		this.mentorId=mentorId;
		this.title=title;
		this.description=description;
	}

}
