package com.soongsil.CoffeeChat.domain.application.entity;

import jakarta.persistence.*;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SQLRestriction;

import com.soongsil.CoffeeChat.domain.application.enums.ApplicationStatus;
import com.soongsil.CoffeeChat.domain.mentee.entity.Mentee;
import com.soongsil.CoffeeChat.domain.mentor.entity.Mentor;
import com.soongsil.CoffeeChat.domain.possibleDate.entity.PossibleDate;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
@SQLRestriction("accept <> 'REJECTED'")
@Table(name = "Application")
public class Application {
    @Column(name = "application_id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentor_id")
    private Mentor mentor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentee_id")
    private Mentee mentee;

    @Column private String memo;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(255) DEFAULT 'UNMATCHED'")
    private ApplicationStatus accept;

    @ManyToOne
    @JoinColumn(name = "possible_date_id")
    private PossibleDate possibleDate;

    public void acceptApplication() {
        this.accept = ApplicationStatus.MATCHED;
    }

    public void rejectApplication() {
        this.accept = ApplicationStatus.REJECTED;
    }
}
