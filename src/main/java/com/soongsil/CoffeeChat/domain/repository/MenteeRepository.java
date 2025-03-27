package com.soongsil.CoffeeChat.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.soongsil.CoffeeChat.domain.entity.Mentee;

public interface MenteeRepository extends JpaRepository<Mentee, Long> {}
