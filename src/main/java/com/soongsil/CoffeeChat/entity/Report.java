package com.soongsil.CoffeeChat.entity;

import com.soongsil.CoffeeChat.enums.ReportReason;
import com.soongsil.CoffeeChat.enums.ReportStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Report {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
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
}
