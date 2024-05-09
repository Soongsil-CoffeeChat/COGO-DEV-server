package com.soongsil.CoffeeChat.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.soongsil.CoffeeChat.entity.PossibleDate;

public interface PossibleDateRepository extends JpaRepository<PossibleDate, Long> {
}
