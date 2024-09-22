package com.soongsil.CoffeeChat.repository.User;

import com.soongsil.CoffeeChat.dto.UserGetUpdateDto;
import org.springframework.data.jpa.repository.JpaRepository;

import com.soongsil.CoffeeChat.entity.Mentor;
import com.soongsil.CoffeeChat.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {
	Optional<User> findByUsername(String username);

	User findByMentor(Mentor mentor);

	User findByMentorIdWithFetch(Long mentorId);

	User findByUsernameWithFetch(String username);

	User findByMenteeId(Long menteeId);

	UserGetUpdateDto findUserInfoByUsername(String username);
}
