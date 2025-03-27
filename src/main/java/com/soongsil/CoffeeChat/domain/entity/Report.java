package com.soongsil.CoffeeChat.domain.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import com.soongsil.CoffeeChat.domain.dto.ReportDto;
import com.soongsil.CoffeeChat.domain.entity.enums.ReportReason;
import com.soongsil.CoffeeChat.domain.entity.enums.ReportStatus;

import lombok.*;

@Entity
@Getter
@Setter
@Table
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long reporterId;

    // ID of the person being reported (신고 받은 사람 ID, Long 타입)
    private Long reportedUserId;

    // Reason for the report (e.g., abusive language, spam, inappropriate content, etc.)
    private ReportReason reason;

    // Additional explanation provided by the reporter
    private String additionalDetails;

    // Date and time when the report was created
    private LocalDateTime reportedAt;

    // Status of the report (e.g., PENDING, REVIEWED, ACTION_TAKEN)
    private ReportStatus status;

    public static Report from(ReportDto dto, Long reporterId) {
        return Report.builder()
                .reporterId(reporterId)
                .reportedUserId(dto.getReportedUserId())
                .reason(dto.getReason())
                .additionalDetails(dto.getAdditionalDetails())
                .reportedAt(LocalDateTime.now())
                .status(ReportStatus.PENDING)
                .build();
    }
}
