package com.soongsil.CoffeeChat.domain.mentor.repository;

import java.util.List;

import com.soongsil.CoffeeChat.domain.mentor.dto.MentorResponse.*;
import com.soongsil.CoffeeChat.domain.mentor.enums.ClubEnum;
import com.soongsil.CoffeeChat.domain.mentor.enums.PartEnum;

public interface MentorRepositoryCustom {
    List<MentorListResponse> getMentorListByPartAndClub(PartEnum part, ClubEnum club);

    MentorDetailResponse getMentorDetailResponse(Long mentorId);
}
