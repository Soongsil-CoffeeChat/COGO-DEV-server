package com.soongsil.CoffeeChat.entity;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.soongsil.CoffeeChat.dto.PossibleDateRequestDto;

import jakarta.persistence.*;
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

    @JsonFormat(pattern = "HH:mm")  //datetimeformat은 ss까지 전부 다 받아야 오류안남
    LocalTime startTime;

    @JsonFormat(pattern = "HH:mm")
    LocalTime endTime;

    @Column
    @Setter
    @Builder.Default
    private boolean isActive=true;

    public static PossibleDate from(PossibleDateRequestDto dto) {
        return PossibleDate.builder()
                .date(dto.getDate())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .isActive(true)
                .build();
    }
}