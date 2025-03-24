package com.soongsil.CoffeeChat.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.soongsil.CoffeeChat.domain.entity.Report;

public interface ReportRepository extends JpaRepository<Report, Long> {}
