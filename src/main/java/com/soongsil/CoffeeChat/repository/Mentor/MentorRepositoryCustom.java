package com.soongsil.CoffeeChat.repository.Mentor;

import com.soongsil.CoffeeChat.dto.ResponseMentorListInfo;
import com.soongsil.CoffeeChat.entity.User;


import java.util.List;

public interface MentorRepositoryCustom {
    List<ResponseMentorListInfo> getMentorListByPart(String part);
    List<User> getMentorListByPart2(String part);

}
