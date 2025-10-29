package com.soongsil.CoffeeChat.domain.application.entity;

import jakarta.persistence.*;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.soongsil.CoffeeChat.domain.application.enums.ApplicationRejectReason;
import com.soongsil.CoffeeChat.domain.application.enums.ApplicationStatus;
import com.soongsil.CoffeeChat.domain.mentee.entity.Mentee;
import com.soongsil.CoffeeChat.domain.mentor.entity.Mentor;
import com.soongsil.CoffeeChat.domain.possibleDate.entity.PossibleDate;

import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
// @SQLRestriction("accept <> 'REJECTED'")
@Table(name = "Application")
public class Application {
    @Id
    @Column(name = "application_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentor_id")
    private Mentor mentor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentee_id")
    private Mentee mentee;

    @Column private String memo;

    // application 생성 로직에 추가
    @Enumerated(EnumType.STRING)
    @Column(name = "reject_reason")
    private ApplicationRejectReason rejectReason;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(255) DEFAULT 'UNMATCHED'")
    private ApplicationStatus applicationStatus;

    @ManyToOne
    @JoinColumn(name = "possible_date_id")
    private PossibleDate possibleDate;

    public void acceptApplication() {
        this.applicationStatus = ApplicationStatus.MATCHED;
    }

    public void rejectApplication(ApplicationRejectReason reason) {
        this.applicationStatus = ApplicationStatus.REJECTED;
        this.rejectReason = reason;
    }
}
