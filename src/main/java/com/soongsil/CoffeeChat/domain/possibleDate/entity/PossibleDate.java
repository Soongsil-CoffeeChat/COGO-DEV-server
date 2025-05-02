package com.soongsil.CoffeeChat.domain.possibleDate.entity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.soongsil.CoffeeChat.domain.application.entity.Application;
import com.soongsil.CoffeeChat.domain.mentor.entity.Mentor;

import lombok.*;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString(of = {"id", "date", "startTime", "endTime", "isActive"})
public class PossibleDate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "possible_date_id")
    private Long id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentor_id")
    private Mentor mentor;

    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate date;

    @JsonFormat(pattern = "HH:mm") // datetimeformat은 ss까지 전부 다 받아야 오류안남
    LocalTime startTime;

    @JsonFormat(pattern = "HH:mm")
    LocalTime endTime;

    @OneToMany(mappedBy = "possibleDate")
    @Builder.Default
    private List<Application> applications = new ArrayList<>();

    @Column @Builder.Default private boolean isActive = true;

    public void deactivate() {
        isActive = false;
    }
}
