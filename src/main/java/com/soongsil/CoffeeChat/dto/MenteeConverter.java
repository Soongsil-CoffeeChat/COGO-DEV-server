package com.soongsil.CoffeeChat.dto;

import com.soongsil.CoffeeChat.dto.MenteeRequest.MenteeJoinRequest;
import com.soongsil.CoffeeChat.dto.MenteeResponse.MenteeInfoResponse;
import com.soongsil.CoffeeChat.entity.Mentee;

public class MenteeConverter {
    public static Mentee toEntity(MenteeJoinRequest dto) {
        return Mentee.builder().part(dto.getPart()).build();
    }

    public static MenteeInfoResponse toResponse(Mentee mentee) {
        return MenteeInfoResponse.builder().part(mentee.getPart()).isNewAccount(false).build();
    }
}
