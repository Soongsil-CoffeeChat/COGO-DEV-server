package com.soongsil.CoffeeChat.domain.user.repository;

import com.soongsil.CoffeeChat.domain.user.dto.UserResponse.UserGetResponse;
import com.soongsil.CoffeeChat.domain.user.entity.User;

public interface UserRepositoryCustom {
    User findByMentorIdWithFetch(Long mentorId);

    User findByUsernameWithFetch(String username);

    UserGetResponse findUserInfoByUsername(String username);
}
