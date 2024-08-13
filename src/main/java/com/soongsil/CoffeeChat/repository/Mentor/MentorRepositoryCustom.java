package com.soongsil.CoffeeChat.repository.Mentor;

import java.util.List;

import com.soongsil.CoffeeChat.dto.MentorGetListResponseDto;
import com.soongsil.CoffeeChat.dto.MentorGetUpdateDetailDto;
import com.soongsil.CoffeeChat.entity.User;
import com.soongsil.CoffeeChat.enums.ClubEnum;
import com.soongsil.CoffeeChat.enums.PartEnum;

public interface MentorRepositoryCustom {
    List<MentorGetListResponseDto> getMentorListByPart(PartEnum part);  //일반 join
    List<MentorGetListResponseDto> getMentorListByClub(ClubEnum club);
    List<MentorGetListResponseDto> getMentorListByPartAndClub(PartEnum part, ClubEnum club);
    List<User> getMentorListByPartWithFetch(PartEnum part); //fetch join

    MentorGetUpdateDetailDto getMentorInfoByMentorId(Long mentorId);

}
