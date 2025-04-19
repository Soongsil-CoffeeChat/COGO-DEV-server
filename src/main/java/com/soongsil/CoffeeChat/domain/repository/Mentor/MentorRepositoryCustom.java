package com.soongsil.CoffeeChat.domain.repository.Mentor;

import java.util.List;

import com.soongsil.CoffeeChat.domain.dto.MentorResponse.*;
import com.soongsil.CoffeeChat.domain.entity.enums.ClubEnum;
import com.soongsil.CoffeeChat.domain.entity.enums.PartEnum;

public interface MentorRepositoryCustom {
    List<MentorListResponse> getMentorListByPartAndClub(PartEnum part, ClubEnum club);

    MentorDetailResponse getMentorInfoByMentorId(Long mentorId);
}
