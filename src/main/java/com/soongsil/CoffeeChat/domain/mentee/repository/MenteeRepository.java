package com.soongsil.CoffeeChat.domain.mentee.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.soongsil.CoffeeChat.domain.mentee.entity.Mentee;

public interface MenteeRepository extends JpaRepository<Mentee, Long> {}
