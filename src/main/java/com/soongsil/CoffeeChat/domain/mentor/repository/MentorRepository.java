package com.soongsil.CoffeeChat.domain.mentor.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.soongsil.CoffeeChat.domain.mentor.entity.Mentor;

public interface MentorRepository extends JpaRepository<Mentor, Long>, MentorRepositoryCustom {}
