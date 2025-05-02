package com.soongsil.CoffeeChat.domain.mentee.dto;

import com.soongsil.CoffeeChat.domain.mentee.dto.MenteeRequest.MenteeJoinRequest;
import com.soongsil.CoffeeChat.domain.mentee.dto.MenteeResponse.MenteeInfoResponse;
import com.soongsil.CoffeeChat.domain.mentee.entity.Mentee;
import com.soongsil.CoffeeChat.domain.user.entity.User;

public class MenteeConverter {
    public static Mentee toEntity(MenteeJoinRequest dto, User user) {
        return Mentee.builder().part(dto.getPart()).user(user).build();
    }

    public static MenteeInfoResponse toResponse(Mentee mentee) {
        return MenteeInfoResponse.builder().part(mentee.getPart()).build();
    }
}
