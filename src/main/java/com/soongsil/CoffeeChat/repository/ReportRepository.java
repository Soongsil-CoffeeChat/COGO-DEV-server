package com.soongsil.CoffeeChat.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.soongsil.CoffeeChat.entity.Report;

public interface ReportRepository extends JpaRepository<Report, Long> {}
