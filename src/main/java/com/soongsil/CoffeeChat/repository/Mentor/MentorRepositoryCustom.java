package com.soongsil.CoffeeChat.repository.Mentor;

import com.soongsil.CoffeeChat.dto.ResponseMentorListInfo;
import com.soongsil.CoffeeChat.entity.User;


import java.util.List;

public interface MentorRepositoryCustom {
    List<ResponseMentorListInfo> getMentorListByPart(int part);  //일반 join
    List<ResponseMentorListInfo> getMentorListByClub(int club);
    List<ResponseMentorListInfo> getMentorListByPartAndClub(int part, int club);
    List<User> getMentorListByPart2(int part); //fetch join

}
