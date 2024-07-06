package com.soongsil.CoffeeChat.dto;

import java.util.ArrayList;
import java.util.List;

import com.querydsl.core.annotations.QueryProjection;
import com.soongsil.CoffeeChat.entity.Club;
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
	//private List<String> clubName;
	private String field;
	private String username;
	private String part;

	@QueryProjection
	public ResponseMentorListInfo(String picture, String mentorName, String field, String username, String part) {
		this.picture = picture;
		this.mentorName = mentorName;
		this.field = field;
		this.username = username;
		this.part = part;
	}

	public static ResponseMentorListInfo toDto(Mentor mentor, User user) {
		List<Club> clubList = mentor.getClubs();
		List<String> clubNameList = new ArrayList<>();
		for (Club club : clubList) {
			clubNameList.add(club.getName());
		}
		return ResponseMentorListInfo.builder()
				.part(mentor.getPart())
			.picture(mentor.getPicture())
			.mentorName(user.getName())
			//.clubName(clubNameList)
			.field(mentor.getField())
			.username(user.getUsername())
			.build();
	}
}
