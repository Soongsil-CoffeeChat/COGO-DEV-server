package com.soongsil.CoffeeChat.domain.mentor.repository;

import com.soongsil.CoffeeChat.domain.mentor.dto.MentorResponse.MentorDetailResponse;
import com.soongsil.CoffeeChat.domain.mentor.dto.MentorResponse.MentorListResponse;
import com.soongsil.CoffeeChat.domain.mentor.enums.ClubEnum;
import com.soongsil.CoffeeChat.domain.mentor.enums.PartEnum;

import java.util.List;

public interface MentorRepositoryCustom {
    List<MentorListResponse> getMentorListByPartAndClub(PartEnum part, ClubEnum club);

    MentorDetailResponse getMentorInfoByMentorId(Long mentorId);
}
