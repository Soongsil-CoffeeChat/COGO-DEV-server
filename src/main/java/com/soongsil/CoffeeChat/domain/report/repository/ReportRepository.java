package com.soongsil.CoffeeChat.domain.report.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.soongsil.CoffeeChat.domain.report.entity.Report;

public interface ReportRepository extends JpaRepository<Report, Long> {}
