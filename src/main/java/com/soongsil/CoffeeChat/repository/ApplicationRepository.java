package com.soongsil.CoffeeChat.repository;

import java.util.List;

import com.soongsil.CoffeeChat.entity.Mentor;
import org.springframework.data.jpa.repository.JpaRepository;

import com.soongsil.CoffeeChat.entity.Application;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
	List<Application> findApplicationByMentor(Mentor mentor);
}
