package com.soongsil.CoffeeChat.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.soongsil.CoffeeChat.entity.Mentor;
import com.soongsil.CoffeeChat.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	User findByUsername(String username);

	User findByMentor(Mentor mentor);

	User findByMentorId(Long mentorId);
}
