package com.soongsil.CoffeeChat.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.soongsil.CoffeeChat.dto.ApplicationGetResponse;
import com.soongsil.CoffeeChat.entity.Application;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
	List<ApplicationGetResponse> findApplicationsByMentorId(Long mentorId);
}
