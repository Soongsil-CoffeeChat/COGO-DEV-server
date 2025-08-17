package com.soongsil.CoffeeChat.domain.mentor.repository;

import com.soongsil.CoffeeChat.domain.mentor.entity.Mentor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MentorRepository extends JpaRepository<Mentor, Long>, MentorRepositoryCustom {}
