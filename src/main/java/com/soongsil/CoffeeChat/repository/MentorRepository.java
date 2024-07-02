package com.soongsil.CoffeeChat.repository;

import java.util.List;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.jpa.repository.JpaRepository;

import com.soongsil.CoffeeChat.entity.Mentor;

public interface MentorRepository extends JpaRepository<Mentor, Long> {
	//Mentor findByUsername(String username); 상속시 사용가능

	List<Mentor> findAllByPart(String part);
}
