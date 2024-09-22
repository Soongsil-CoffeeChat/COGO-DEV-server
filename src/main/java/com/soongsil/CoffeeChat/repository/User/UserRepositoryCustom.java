package com.soongsil.CoffeeChat.repository.User;

import com.soongsil.CoffeeChat.dto.UserGetUpdateDto;
import com.soongsil.CoffeeChat.entity.User;

public interface UserRepositoryCustom {
	User findByMentorIdWithFetch(Long mentorId);

	User findByUsernameWithFetch(String username);

	UserGetUpdateDto findUserInfoByUsername(String username);
}
