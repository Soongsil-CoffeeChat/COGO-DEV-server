package com.soongsil.CoffeeChat.repository.User;

import org.springframework.data.jpa.repository.JpaRepository;

import com.soongsil.CoffeeChat.entity.Mentor;
import com.soongsil.CoffeeChat.entity.User;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {
	User findByUsername(String username);

	User findByMentor(Mentor mentor);

	User findByMentorIdWithFetch(Long mentorId);

	User findByUsernameWithFetch(String username);

	User findByMenteeId(Long menteeId);
}
