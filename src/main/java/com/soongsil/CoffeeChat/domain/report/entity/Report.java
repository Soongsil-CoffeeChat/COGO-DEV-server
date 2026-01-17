package com.soongsil.CoffeeChat.domain.report.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.soongsil.CoffeeChat.domain.report.enums.ReportReason;
import com.soongsil.CoffeeChat.domain.report.enums.ReportStatus;

import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long reporterId;

    @Column
    private Long reportedUserId;

    @Enumerated(EnumType.STRING)
    @Column
    private ReportReason reason;

    @Column
    private String additionalDetails;


    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    @Column
    private LocalDateTime reportedAt;

    @Enumerated(EnumType.STRING)
    @Column
    private ReportStatus status;
}
