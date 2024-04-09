package com.soongsil.CoffeeChat.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.soongsil.CoffeeChat.entity.Refresh;

import jakarta.transaction.Transactional;

public interface RefreshRepository extends JpaRepository<Refresh, Long> {
	Boolean existsByRefresh(String refresh);

	@Transactional
	void deleteByRefresh(String refresh);
}
