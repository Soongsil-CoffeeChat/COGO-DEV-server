package com.soongsil.CoffeeChat.repository.Mentor;

import com.soongsil.CoffeeChat.dto.ResponseMentorInfo;
import com.soongsil.CoffeeChat.dto.ResponseMentorListInfo;
import com.soongsil.CoffeeChat.entity.User;
import com.soongsil.CoffeeChat.enums.ClubEnum;
import com.soongsil.CoffeeChat.enums.PartEnum;


import java.util.List;

public interface MentorRepositoryCustom {
    List<ResponseMentorListInfo> getMentorListByPart(PartEnum part);  //일반 join
    List<ResponseMentorListInfo> getMentorListByClub(ClubEnum club);
    List<ResponseMentorListInfo> getMentorListByPartAndClub(PartEnum part, ClubEnum club);
    List<User> getMentorListByPartWithFetch(PartEnum part); //fetch join

    ResponseMentorInfo getMentorInfoByMentorId(Long mentorId);

}
