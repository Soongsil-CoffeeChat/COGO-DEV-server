package com.soongsil.CoffeeChat.domain.repository;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.soongsil.CoffeeChat.domain.entity.Refresh;

public interface RefreshRepository extends JpaRepository<Refresh, Long> {
    Boolean existsByRefresh(String refresh);

    @Transactional
    void deleteByRefresh(String refresh);
}
