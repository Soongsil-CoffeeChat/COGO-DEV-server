package com.soongsil.CoffeeChat.domain.report.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.soongsil.CoffeeChat.domain.report.entity.Report;

public interface ReportRepository extends JpaRepository<Report, Long> {

    // 신고한 유저 ID 목록 조회
    @Query("SELECT r.reportedUerId FROM Report r WHERE r.reporterId = :reporterId")
    List<Long> findReportedUserIdsByReporterId(@Param("reporterId") Long reporterId);

    // 사용자 간 신고 내영 존재 확인
    boolean existsByReporterIdAndReportedUserId(Long reporterId, Long reportedUserId);
}
