package com.soongsil.CoffeeChat.dto;

import java.util.ArrayList;
import java.util.List;

import com.querydsl.core.annotations.QueryProjection;
import com.soongsil.CoffeeChat.entity.Mentor;
import com.soongsil.CoffeeChat.entity.User;

import com.soongsil.CoffeeChat.enums.ClubEnum;
import com.soongsil.CoffeeChat.enums.PartEnum;
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
	private PartEnum part;
	private ClubEnum club;
	private String username;
	private Long mentorId;
	private String title;
	private String description;


	@QueryProjection
	public ResponseMentorListInfo(String picture, String mentorName, PartEnum part, ClubEnum club, String username,Long mentorId, String title, String description) {
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
