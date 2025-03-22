package com.soongsil.CoffeeChat.repository.Mentor;

import java.util.List;

import com.soongsil.CoffeeChat.dto.MentorResponse.*;
import com.soongsil.CoffeeChat.entity.User;
import com.soongsil.CoffeeChat.enums.ClubEnum;
import com.soongsil.CoffeeChat.enums.PartEnum;

public interface MentorRepositoryCustom {
    List<MentorListResponse> getMentorListByPart(PartEnum part); // 일반 join

    List<MentorListResponse> getMentorListByClub(ClubEnum club);

    List<MentorListResponse> getMentorListByPartAndClub(PartEnum part, ClubEnum club);

    List<User> getMentorListByPartWithFetch(PartEnum part); // fetch join

    MentorGetUpdateDetailResponse getMentorInfoByMentorId(Long mentorId);
}
