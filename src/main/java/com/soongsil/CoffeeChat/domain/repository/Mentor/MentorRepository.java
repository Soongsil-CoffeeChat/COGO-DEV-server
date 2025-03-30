package com.soongsil.CoffeeChat.domain.repository.Mentor;

import org.springframework.data.jpa.repository.JpaRepository;

import com.soongsil.CoffeeChat.domain.entity.Mentor;

public interface MentorRepository extends JpaRepository<Mentor, Long>, MentorRepositoryCustom {}
