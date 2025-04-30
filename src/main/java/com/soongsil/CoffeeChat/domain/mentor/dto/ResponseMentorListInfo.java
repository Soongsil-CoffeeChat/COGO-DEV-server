package com.soongsil.CoffeeChat.domain.mentor.dto;

import com.querydsl.core.annotations.QueryProjection;

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

    @QueryProjection
    public ResponseMentorListInfo(
            String picture, String mentorName, int part, int club, String username) {
        this.picture = picture;
        this.mentorName = mentorName;
        this.part = part;
        this.club = club;
        this.username = username;
    }
}
