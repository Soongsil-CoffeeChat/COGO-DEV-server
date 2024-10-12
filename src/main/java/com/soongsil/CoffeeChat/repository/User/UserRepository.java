package com.soongsil.CoffeeChat.repository.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.soongsil.CoffeeChat.dto.UserGetDto;
import com.soongsil.CoffeeChat.entity.Mentor;
import com.soongsil.CoffeeChat.entity.User;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {
	Optional<User> findByUsername(String username);

	User findByMentor(Mentor mentor);

	User findByMentorIdWithFetch(Long mentorId);

	User findByUsernameWithFetch(String username);

	User findByMenteeId(Long menteeId);

	UserGetDto findUserInfoByUsername(String username);
}
