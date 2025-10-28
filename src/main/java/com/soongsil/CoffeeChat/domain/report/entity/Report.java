package com.soongsil.CoffeeChat.domain.report.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;

import com.soongsil.CoffeeChat.domain.report.enums.ReportReason;
import com.soongsil.CoffeeChat.domain.report.enums.ReportStatus;

import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long reporterId;

    @Column
    private Long reportedUserId;

    @Column
    private ReportReason reason;

    @Column
    private String additionalDetails;

    @Column
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime reportedAt;

    // Status of the report (e.g., PENDING, REVIEWED, ACTION_TAKEN)
    @Enumerated(EnumType.STRING)
    @Column
    private ReportStatus status;

}
