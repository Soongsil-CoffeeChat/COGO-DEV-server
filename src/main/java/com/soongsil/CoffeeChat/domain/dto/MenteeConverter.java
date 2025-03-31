package com.soongsil.CoffeeChat.domain.dto;

import com.soongsil.CoffeeChat.domain.dto.MenteeRequest.MenteeJoinRequest;
import com.soongsil.CoffeeChat.domain.dto.MenteeResponse.MenteeInfoResponse;
import com.soongsil.CoffeeChat.domain.entity.Mentee;
import com.soongsil.CoffeeChat.domain.entity.User;

public class MenteeConverter {
    public static Mentee toEntity(MenteeJoinRequest dto, User user) {
        return Mentee.builder().part(dto.getPart()).user(user).build();
    }

    public static MenteeInfoResponse toResponse(Mentee mentee) {
        return MenteeInfoResponse.builder().part(mentee.getPart()).build();
    }
}
