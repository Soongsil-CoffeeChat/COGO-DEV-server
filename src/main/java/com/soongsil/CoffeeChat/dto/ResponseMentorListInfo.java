package com.soongsil.CoffeeChat.dto;

import com.soongsil.CoffeeChat.entity.Club;
import com.soongsil.CoffeeChat.entity.Mentor;
import com.soongsil.CoffeeChat.entity.User;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

@Builder
public class ResponseMentorListInfo {
    private String picture;
    private String mentorName;
    private List<String> clubName;
    private String field;

    public static ResponseMentorListInfo toDto(Mentor mentor, User user){
        List<Club> clubList=mentor.getClubs();
        List<String> clubNameList=new ArrayList<>();
        for(Club club:clubList){
            clubNameList.add(club.getName());
        }
        return ResponseMentorListInfo.builder()
                .picture(user.getPicture())
                .mentorName(user.getName())
                .clubName(clubNameList)
                .field(mentor.getField())
                .build();
    }
}
