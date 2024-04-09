package com.soongsil.CoffeeChat.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.soongsil.CoffeeChat.entity.Mentee;

public interface MenteeRepository extends JpaRepository<Mentee, Long> {

}
