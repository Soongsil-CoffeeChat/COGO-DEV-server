package com.soongsil.CoffeeChat.domain.mentee.repository;

import com.soongsil.CoffeeChat.domain.mentee.entity.Mentee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenteeRepository extends JpaRepository<Mentee, Long> {}
