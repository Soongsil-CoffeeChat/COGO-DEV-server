package com.soongsil.CoffeeChat.domain.repository.User;

import com.soongsil.CoffeeChat.domain.dto.UserRequest.*;
import com.soongsil.CoffeeChat.domain.entity.User;

public interface UserRepositoryCustom {
    User findByMentorIdWithFetch(Long mentorId);

    User findByUsernameWithFetch(String username);

    UserGetRequest findUserInfoByUsername(String username);
}
