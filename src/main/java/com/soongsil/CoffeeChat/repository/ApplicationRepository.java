package com.soongsil.CoffeeChat.repository;

import java.util.List;

import com.soongsil.CoffeeChat.entity.Mentee;
import org.springframework.data.jpa.repository.JpaRepository;

import com.soongsil.CoffeeChat.entity.Application;
import com.soongsil.CoffeeChat.entity.Mentor;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
	List<Application> findApplicationByMentor(Mentor mentor);
	List<Application> findApplicationByMentee(Mentee mentee);
}
