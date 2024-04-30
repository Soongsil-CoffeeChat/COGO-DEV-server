package com.soongsil.CoffeeChat.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.soongsil.CoffeeChat.entity.Application;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
}
