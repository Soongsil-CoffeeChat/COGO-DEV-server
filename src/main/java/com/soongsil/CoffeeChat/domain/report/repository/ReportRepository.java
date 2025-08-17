package com.soongsil.CoffeeChat.domain.report.repository;

import com.soongsil.CoffeeChat.domain.report.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {}
