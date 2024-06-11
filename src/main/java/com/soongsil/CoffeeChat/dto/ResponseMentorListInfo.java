package com.soongsil.CoffeeChat.dto;

import java.util.ArrayList;
import java.util.List;

import com.soongsil.CoffeeChat.entity.Club;
import com.soongsil.CoffeeChat.entity.Mentor;
import com.soongsil.CoffeeChat.entity.User;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ResponseMentorListInfo {
	private String picture;
	private String mentorName;
	private List<String> clubName;
	private String field;
	private String username;
	private String part;

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
			.clubName(clubNameList)
			.field(mentor.getField())
			.username(user.getUsername())
			.build();
	}
}
